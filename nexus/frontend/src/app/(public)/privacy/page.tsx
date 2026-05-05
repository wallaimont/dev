import Link from 'next/link'

export const metadata = { title: 'Política de Privacidade' }

export default function PrivacyPage() {
  return (
    <div className="min-h-screen bg-[var(--bg-page)] px-4 py-12">
      <div className="mx-auto max-w-3xl bg-white rounded-[20px] border border-[var(--border-light)] p-8 md:p-12 shadow-[var(--shadow-card)]">
        <Link href="/login" className="text-sm text-nexus-500 font-medium">← Voltar</Link>
        <h1 className="font-display text-3xl font-800 tracking-tight mt-4 mb-6">Política de Privacidade</h1>
        <div className="prose prose-sm text-[var(--text-secondary)] max-w-none space-y-4">
          <p><strong>Última atualização:</strong> 30 de março de 2026</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">1. Dados Coletados</h2>
          <p>Coletamos dados pessoais necessários para a prestação do serviço: nome, e-mail, telefone, endereço de entrega e informações de pagamento.</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">2. Finalidade do Tratamento</h2>
          <p>Seus dados são utilizados para processar pedidos, personalizar sua experiência, prevenir fraudes e cumprir obrigações legais, conforme a LGPD (Lei 13.709/2018).</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">3. Compartilhamento</h2>
          <p>Compartilhamos dados apenas com vendedores (para envio de produtos), processadores de pagamento e autoridades quando exigido por lei.</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">4. Armazenamento e Segurança</h2>
          <p>Os dados são armazenados em servidores seguros com criptografia em trânsito e em repouso. Adotamos medidas técnicas e administrativas para proteção dos dados.</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">5. Seus Direitos (LGPD)</h2>
          <p>Você tem direito a: acessar, corrigir, excluir, portar seus dados, revogar consentimento e solicitar informações sobre o tratamento. Exercite seus direitos pelo e-mail <strong>privacidade@nexus.com.br</strong>.</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">6. Cookies</h2>
          <p>Utilizamos cookies essenciais para autenticação e funcionamento da plataforma. Cookies analíticos podem ser desativados nas configurações do navegador.</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">7. Retenção de Dados</h2>
          <p>Mantemos seus dados enquanto sua conta estiver ativa ou conforme necessário para obrigações legais. Após exclusão da conta, os dados são anonimizados em até 30 dias.</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">8. Contato do DPO</h2>
          <p>Encarregado de Proteção de Dados: <strong>dpo@nexus.com.br</strong></p>
        </div>
      </div>
    </div>
  )
}
