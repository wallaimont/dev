package com.seguradora.site;

import com.seguradora.site.model.LeadRequest;
import com.seguradora.site.model.QuoteResult;
import com.seguradora.site.service.FormDuplicateSubmissionGuardService;
import com.seguradora.site.service.FormInputNormalizationService;
import com.seguradora.site.service.FormSubmissionTelemetryService;
import com.seguradora.site.service.FormValidationFeatureFlagService;
import com.seguradora.site.service.LeadStorageService;
import com.seguradora.site.service.QuoteService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class SiteController {

    private static final Logger log = LoggerFactory.getLogger(SiteController.class);

    private final QuoteService quoteService;
    private final LeadStorageService leadStorageService;
    private final FormValidationFeatureFlagService formValidationFeatureFlagService;
    private final FormInputNormalizationService formInputNormalizationService;
    private final FormDuplicateSubmissionGuardService formDuplicateSubmissionGuardService;
    private final FormSubmissionTelemetryService formSubmissionTelemetryService;

    public SiteController(
            QuoteService quoteService,
            LeadStorageService leadStorageService,
            FormValidationFeatureFlagService formValidationFeatureFlagService,
            FormInputNormalizationService formInputNormalizationService,
            FormDuplicateSubmissionGuardService formDuplicateSubmissionGuardService,
            FormSubmissionTelemetryService formSubmissionTelemetryService
    ) {
        this.quoteService = quoteService;
        this.leadStorageService = leadStorageService;
        this.formValidationFeatureFlagService = formValidationFeatureFlagService;
        this.formInputNormalizationService = formInputNormalizationService;
        this.formDuplicateSubmissionGuardService = formDuplicateSubmissionGuardService;
        this.formSubmissionTelemetryService = formSubmissionTelemetryService;
    }

    @ModelAttribute("tiposSeguro")
    public List<String> tiposSeguro() {
        return List.of("Seguro Auto", "Seguro Residencial", "Seguro Empresarial", "Seguro de Vida");
    }

    @ModelAttribute("coberturas")
    public List<String> coberturas() {
        return List.of("Essencial", "Completa", "Premium");
    }

    @GetMapping("/")
    public String home(Model model) {
        populateSharedContent(model);
        model.addAttribute("currentPage", "home");
        model.addAttribute("leadCount", leadStorageService.count());
        return "index";
    }

    @GetMapping("/seguros")
    public String seguros(Model model) {
        populateSharedContent(model);
        model.addAttribute("currentPage", "seguros");
        model.addAttribute("planos", List.of(
                List.of("Seguro Auto", "Cobertura contra colisão, roubo e assistência 24h", "A partir de R$ 149/mês"),
                List.of("Seguro Residencial", "Proteção para imóvel, conteúdo e responsabilidade civil", "A partir de R$ 79/mês"),
                List.of("Seguro Empresarial", "Cobertura patrimonial, lucros cessantes e equipamentos", "A partir de R$ 219/mês"),
                List.of("Seguro de Vida", "Indenização, assistência familiar e invalidez", "A partir de R$ 59/mês")
        ));
        return "seguros";
    }

    @GetMapping("/cotacao")
    public String cotacao(@RequestParam(name = "tipo", required = false) String tipo, Model model) {
        populateSharedContent(model);
        model.addAttribute("currentPage", "cotacao");

        LeadRequest leadRequest = new LeadRequest();
        leadRequest.setTipoSeguro(tipo);
        leadRequest.setCobertura("Completa");
        leadRequest.setOrigem("cotacao");
        leadRequest.setSubmissionToken(formDuplicateSubmissionGuardService.generateToken());

        model.addAttribute("leadRequest", leadRequest);
        return "cotacao";
    }

    @PostMapping("/cotacao")
    public String enviarCotacao(@Valid @ModelAttribute LeadRequest leadRequest, BindingResult bindingResult, Model model) {
        long startedAt = System.nanoTime();
        String submissionId = resolveSubmissionId(leadRequest);

        populateSharedContent(model);
        model.addAttribute("currentPage", "cotacao");
        model.addAttribute("submissionId", submissionId);

        leadRequest.setOrigem("cotacao");
        boolean enhancedValidationEnabled = formValidationFeatureFlagService.isEnhancedValidationEnabled("cotacao");

        if (enhancedValidationEnabled) {
            formInputNormalizationService.normalize(leadRequest);
            applyCotacaoRules(leadRequest, bindingResult);
        }

        if (bindingResult.hasErrors()) {
            Map<String, List<String>> fieldErrors = toFieldErrors(bindingResult);
            addValidationFeedback(model, fieldErrors);
            leadRequest.setSubmissionToken(formDuplicateSubmissionGuardService.generateToken());
            model.addAttribute("leadRequest", leadRequest);
            formSubmissionTelemetryService.validationError(
                    submissionId,
                    "cotacao",
                    fieldErrors,
                    elapsedMs(startedAt)
            );
            return "cotacao";
        }

        if (enhancedValidationEnabled && formDuplicateSubmissionGuardService.isDuplicate("cotacao", leadRequest.getSubmissionToken())) {
            bindingResult.rejectValue(
                    "submissionToken",
                    "lead.submission.duplicate",
                    "Identificamos um envio duplicado. Aguarde alguns segundos e tente novamente."
            );
            Map<String, List<String>> fieldErrors = toFieldErrors(bindingResult);
            addValidationFeedback(model, fieldErrors);
            leadRequest.setSubmissionToken(formDuplicateSubmissionGuardService.generateToken());
            model.addAttribute("leadRequest", leadRequest);
            formSubmissionTelemetryService.validationError(
                    submissionId,
                    "cotacao",
                    fieldErrors,
                    elapsedMs(startedAt)
            );
            return "cotacao";
        }

        try {
            QuoteResult quoteResult = quoteService.generate(leadRequest);
            leadStorageService.save(leadRequest, quoteResult);

            leadRequest.setSubmissionToken(formDuplicateSubmissionGuardService.generateToken());
            model.addAttribute("leadRequest", leadRequest);
            model.addAttribute("quoteResult", quoteResult);
            model.addAttribute("successMessage", "Solicitação recebida com sucesso. Preparamos uma estimativa inicial para você.");
            formSubmissionTelemetryService.success(submissionId, "cotacao", elapsedMs(startedAt));
        } catch (Exception e) {
            log.error("Erro ao processar cotação para {}: {}", leadRequest.getEmail(), e.getMessage(), e);
            model.addAttribute("errorMessage", "Ocorreu um erro ao processar sua cotação. Tente novamente em instantes.");
            leadRequest.setSubmissionToken(formDuplicateSubmissionGuardService.generateToken());
            model.addAttribute("leadRequest", leadRequest);
            formSubmissionTelemetryService.internalError(submissionId, "cotacao", elapsedMs(startedAt));
        }
        return "cotacao";
    }

    @GetMapping("/contato")
    public String contato(Model model) {
        populateSharedContent(model);
        model.addAttribute("currentPage", "contato");

        LeadRequest leadRequest = new LeadRequest();
        leadRequest.setOrigem("contato");
        leadRequest.setSubmissionToken(formDuplicateSubmissionGuardService.generateToken());
        model.addAttribute("leadRequest", leadRequest);
        return "contato";
    }

    @PostMapping("/contato")
    public String enviarContato(@Valid @ModelAttribute LeadRequest leadRequest, BindingResult bindingResult, Model model) {
        long startedAt = System.nanoTime();
        String submissionId = resolveSubmissionId(leadRequest);

        populateSharedContent(model);
        model.addAttribute("currentPage", "contato");
        model.addAttribute("submissionId", submissionId);

        leadRequest.setOrigem("contato");
        boolean enhancedValidationEnabled = formValidationFeatureFlagService.isEnhancedValidationEnabled("contato");

        if (enhancedValidationEnabled) {
            formInputNormalizationService.normalize(leadRequest);
            applyContatoRules(leadRequest, bindingResult);
        }

        if (bindingResult.hasErrors()) {
            Map<String, List<String>> fieldErrors = toFieldErrors(bindingResult);
            addValidationFeedback(model, fieldErrors);
            leadRequest.setSubmissionToken(formDuplicateSubmissionGuardService.generateToken());
            model.addAttribute("leadRequest", leadRequest);
            formSubmissionTelemetryService.validationError(
                    submissionId,
                    "contato",
                    fieldErrors,
                    elapsedMs(startedAt)
            );
            return "contato";
        }

        if (enhancedValidationEnabled && formDuplicateSubmissionGuardService.isDuplicate("contato", leadRequest.getSubmissionToken())) {
            bindingResult.rejectValue(
                    "submissionToken",
                    "lead.submission.duplicate",
                    "Identificamos um envio duplicado. Aguarde alguns segundos e tente novamente."
            );
            Map<String, List<String>> fieldErrors = toFieldErrors(bindingResult);
            addValidationFeedback(model, fieldErrors);
            leadRequest.setSubmissionToken(formDuplicateSubmissionGuardService.generateToken());
            model.addAttribute("leadRequest", leadRequest);
            formSubmissionTelemetryService.validationError(
                    submissionId,
                    "contato",
                    fieldErrors,
                    elapsedMs(startedAt)
            );
            return "contato";
        }

        try {
            leadStorageService.save(leadRequest);
            LeadRequest nextRequest = new LeadRequest();
            nextRequest.setOrigem("contato");
            nextRequest.setSubmissionToken(formDuplicateSubmissionGuardService.generateToken());
            model.addAttribute("leadRequest", nextRequest);
            model.addAttribute("successMessage", "Mensagem enviada. Nosso time comercial responderá em breve.");
            model.addAttribute("leadCount", leadStorageService.count());
            formSubmissionTelemetryService.success(submissionId, "contato", elapsedMs(startedAt));
        } catch (Exception e) {
            log.error("Erro ao processar contato para {}: {}", leadRequest.getEmail(), e.getMessage(), e);
            model.addAttribute("errorMessage", "Ocorreu um erro ao enviar sua mensagem. Tente novamente em instantes.");
            leadRequest.setSubmissionToken(formDuplicateSubmissionGuardService.generateToken());
            model.addAttribute("leadRequest", leadRequest);
            formSubmissionTelemetryService.internalError(submissionId, "contato", elapsedMs(startedAt));
        }
        return "contato";
    }

    private String resolveSubmissionId(LeadRequest leadRequest) {
        if (leadRequest != null && leadRequest.getSubmissionToken() != null && !leadRequest.getSubmissionToken().isBlank()) {
            return leadRequest.getSubmissionToken();
        }
        return UUID.randomUUID().toString();
    }

    private void applyCotacaoRules(LeadRequest leadRequest, BindingResult bindingResult) {
        if (isBlank(leadRequest.getTipoSeguro())) {
            bindingResult.rejectValue("tipoSeguro", "lead.tipoSeguro.required", "Informe o tipo de seguro.");
        }
        if (isBlank(leadRequest.getCobertura())) {
            bindingResult.rejectValue("cobertura", "lead.cobertura.required", "Informe a cobertura desejada.");
        }
        if (leadRequest.getValorBem() == null) {
            bindingResult.rejectValue("valorBem", "lead.valorBem.required", "Informe o valor aproximado do bem.");
        }
    }

    private void applyContatoRules(LeadRequest leadRequest, BindingResult bindingResult) {
        if (isBlank(leadRequest.getMensagem())) {
            bindingResult.rejectValue("mensagem", "lead.mensagem.required", "Informe a mensagem para contato.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private Map<String, List<String>> toFieldErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(
                        FieldError::getField,
                        LinkedHashMap::new,
                        Collectors.mapping(FieldError::getDefaultMessage, Collectors.toList())
                ));
    }

    private void addValidationFeedback(Model model, Map<String, List<String>> fieldErrors) {
        List<String> errorSummary = fieldErrors.values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        model.addAttribute("fieldErrors", fieldErrors);
        model.addAttribute("errorSummary", errorSummary);
        if (!errorSummary.isEmpty()) {
            model.addAttribute("errorMessage", "Revise os campos destacados e tente novamente.");
        }
    }

    private long elapsedMs(long startedAt) {
        return Duration.ofNanos(System.nanoTime() - startedAt).toMillis();
    }

    private void populateSharedContent(Model model) {
        model.addAttribute("seguros", List.of(
                "Seguro Auto com assistência 24h",
                "Seguro Residencial com cobertura contra imprevistos",
                "Seguro Empresarial para proteção patrimonial",
                "Seguro de Vida para tranquilidade da família"
        ));

        model.addAttribute("beneficios", List.of(
                "Cotação rápida e consultiva",
                "Atendimento humanizado",
                "Planos personalizados para pessoas e empresas",
                "Suporte especializado em sinistros"
        ));

        model.addAttribute("estatisticas", List.of(
                List.of("+12 mil", "clientes protegidos"),
                List.of("97%", "de satisfação no atendimento"),
                List.of("24h", "de suporte emergencial")
        ));
    }
}
