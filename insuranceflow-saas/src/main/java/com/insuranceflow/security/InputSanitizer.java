package com.insuranceflow.security;

import java.util.regex.Pattern;

public final class InputSanitizer {

    private InputSanitizer() {}

    private static final Pattern SCRIPT_PATTERN = Pattern.compile(
        "<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]+>");
    private static final Pattern JAVASCRIPT_PATTERN = Pattern.compile(
        "javascript\\s*:", Pattern.CASE_INSENSITIVE);
    private static final Pattern EVENT_HANDLER_PATTERN = Pattern.compile(
        "on\\w+\\s*=", Pattern.CASE_INSENSITIVE);
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
        "(--|;|/\\*|\\*/|xp_|exec\\s|execute\\s|union\\s+select|insert\\s+into|delete\\s+from|drop\\s+table|alter\\s+table)",
        Pattern.CASE_INSENSITIVE);

    public static String sanitize(String input) {
        if (input == null) return null;
        String cleaned = input;
        cleaned = SCRIPT_PATTERN.matcher(cleaned).replaceAll("");
        cleaned = JAVASCRIPT_PATTERN.matcher(cleaned).replaceAll("");
        cleaned = EVENT_HANDLER_PATTERN.matcher(cleaned).replaceAll("");
        cleaned = cleaned.replace("<", "&lt;").replace(">", "&gt;");
        cleaned = cleaned.replace("'", "&#39;").replace("\"", "&quot;");
        return cleaned.trim();
    }

    public static String sanitizeStrict(String input) {
        if (input == null) return null;
        return HTML_TAG_PATTERN.matcher(input).replaceAll("").trim();
    }

    public static boolean containsSqlInjection(String input) {
        if (input == null) return false;
        return SQL_INJECTION_PATTERN.matcher(input).find();
    }

    public static String sanitizeCnpj(String cnpj) {
        if (cnpj == null) return null;
        return cnpj.replaceAll("[^0-9./\\-]", "");
    }

    public static String sanitizeCpf(String cpf) {
        if (cpf == null) return null;
        return cpf.replaceAll("[^0-9.\\-]", "");
    }

    public static String sanitizeTelefone(String telefone) {
        if (telefone == null) return null;
        return telefone.replaceAll("[^0-9()\\-+ ]", "");
    }

    public static String sanitizeCep(String cep) {
        if (cep == null) return null;
        return cep.replaceAll("[^0-9\\-]", "");
    }
}
