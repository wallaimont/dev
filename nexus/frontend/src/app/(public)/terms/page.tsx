import Link from 'next/link'

export const metadata = { title: 'Termos de Uso' }

export default function TermsPage() {
  return (
    <div className="min-h-screen bg-[var(--bg-page)] px-4 py-12">
      <div className="mx-auto max-w-3xl bg-white rounded-[20px] border border-[var(--border-light)] p-8 md:p-12 shadow-[var(--shadow-card)]">
        <Link href="/login" className="text-sm text-nexus-500 font-medium">← Voltar</Link>
        <h1 className="font-display text-3xl font-800 tracking-tight mt-4 mb-6">Termos de Uso</h1>
        <div className="prose prose-sm text-[var(--text-secondary)] max-w-none space-y-4">
          <p><strong>Última atualização:</strong> 30 de março de 2026</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">1. Aceitação dos Termos</h2>
          <p>Ao acessar ou utilizar a plataforma Nexus Marketplace, você concorda com estes Termos de Uso. Caso não concorde com qualquer disposição, não utilize nossos serviços.</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">2. Descrição do Serviço</h2>
          <p>O Nexus é um marketplace que conecta compradores e vendedores. Não somos responsáveis pelos produtos ou serviços oferecidos por terceiros na plataforma.</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">3. Cadastro e Conta</h2>
          <p>Você é responsável por manter a confidencialidade de suas credenciais de acesso e por todas as atividades realizadas em sua conta.</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">4. Obrigações do Vendedor</h2>
          <p>Vendedores devem fornecer informações precisas sobre seus produtos, cumprir prazos de envio e respeitar as políticas de devolução da plataforma.</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">5. Pagamentos</h2>
          <p>Os pagamentos são processados por intermediadores autorizados. O Nexus retém os valores até a confirmação de recebimento pelo comprador.</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">6. Propriedade Intelectual</h2>
          <p>Todo o conteúdo da plataforma, incluindo marcas, logotipos e software, é de propriedade do Nexus ou de seus licenciadores.</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">7. Limitação de Responsabilidade</h2>
          <p>O Nexus não se responsabiliza por danos indiretos, incidentais ou consequenciais decorrentes do uso da plataforma.</p>

          <h2 className="font-display font-700 text-lg text-[var(--text-primary)]">8. Contato</h2>
          <p>Em caso de dúvidas, entre em contato pelo e-mail <strong>legal@nexus.com.br</strong>.</p>
        </div>
      </div>
    </div>
  )
}
