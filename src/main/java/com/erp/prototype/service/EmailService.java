package com.erp.prototype.service;

import com.erp.prototype.model.OSItem;
import com.erp.prototype.model.OrdemServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.text.NumberFormat;
import java.util.Locale;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Enviado quando uma nova Ordem de Serviço é aberta.
     * O email é enviado para o endereço cadastrado no cliente.
     */
    public void sendEmailAbertura(OrdemServico os) {
        String emailCliente = os.getCliente().getEmail();
        if (emailCliente == null || emailCliente.isBlank()) {
            System.out.println("[EmailService] Cliente sem email cadastrado — email de abertura não enviado. OS #" + os.getId());
            return;
        }

        String assunto = String.format("[Piston & Wood] OS #%d Aberta — Serviço em Andamento", os.getId());

        StringBuilder corpo = new StringBuilder();
        corpo.append("<html><body style=\"font-family: Arial, sans-serif; color: #1a1a2e; background: #f5f5f5; padding: 24px;\">");
        corpo.append("<div style=\"max-width:600px; margin:0 auto; background:#fff; border-radius:12px; overflow:hidden; box-shadow:0 4px 16px rgba(0,0,0,0.08);\">");

        // Cabeçalho
        corpo.append("<div style=\"background:#6d28d9; padding:24px 32px;\">");
        corpo.append("<h1 style=\"margin:0; color:#fff; font-size:20px;\">🔧 Ordem de Serviço Aberta</h1>");
        corpo.append("<p style=\"margin:6px 0 0; color:#ddd6fe; font-size:13px;\">Piston & Wood — Sistema de Gestão</p>");
        corpo.append("</div>");

        // Saudação
        corpo.append("<div style=\"padding:32px;\">");
        corpo.append("<p style=\"color:#374151; font-size:15px;\">Olá, <strong>").append(os.getCliente().getNome()).append("</strong>!</p>");
        corpo.append("<p style=\"color:#6b7280; font-size:14px; margin-top:4px;\">Sua ordem de serviço foi registrada com sucesso. Confira os detalhes abaixo:</p>");

        // Detalhes
        corpo.append("<table style=\"width:100%; border-collapse:collapse; margin-top:20px;\">");
        appendRow(corpo, "Número da OS", "#" + os.getId());
        appendRow(corpo, "Categoria", os.getServicoCategoria() != null ? os.getServicoCategoria().getDescricao() : "—");
        appendRow(corpo, "Data de Abertura", os.getData() != null ? os.getData().toString().replace("T", " ").substring(0, 16) : "—");
        appendRow(corpo, "Status", "Em Andamento");
        corpo.append("</table>");

        // Descrição
        corpo.append("<div style=\"margin-top:24px; padding:16px; background:#f5f3ff; border-left:4px solid #6d28d9; border-radius:4px;\">");
        corpo.append("<strong style=\"color:#5b21b6;\">Descrição do Serviço:</strong><br/>");
        corpo.append("<p style=\"margin:8px 0 0; color:#374151;\">").append(os.getDescricaoServico()).append("</p>");
        corpo.append("</div>");

        corpo.append("<p style=\"margin-top:24px; color:#6b7280; font-size:13px;\">Acompanharemos seu serviço e entraremos em contato assim que estiver concluído. Qualquer dúvida, estamos à disposição.</p>");
        corpo.append("</div>");

        // Rodapé
        rodape(corpo);
        corpo.append("</div></body></html>");

        enviar(emailCliente, assunto, corpo.toString());
    }

    /**
     * Enviado quando uma Ordem de Serviço é concluída, com valor e peças usadas.
     * O email é enviado para o endereço cadastrado no cliente.
     */
    public void sendEmailConclusao(OrdemServico os) {
        String emailCliente = os.getCliente().getEmail();
        if (emailCliente == null || emailCliente.isBlank()) {
            System.out.println("[EmailService] Cliente sem email cadastrado — email de conclusão não enviado. OS #" + os.getId());
            return;
        }

        NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        String assunto = String.format("[Piston & Wood] OS #%d Concluída — Serviço Finalizado", os.getId());

        StringBuilder corpo = new StringBuilder();
        corpo.append("<html><body style=\"font-family: Arial, sans-serif; color: #1a1a2e; background: #f5f5f5; padding: 24px;\">");
        corpo.append("<div style=\"max-width:600px; margin:0 auto; background:#fff; border-radius:12px; overflow:hidden; box-shadow:0 4px 16px rgba(0,0,0,0.08);\">");

        // Cabeçalho
        corpo.append("<div style=\"background:#059669; padding:24px 32px;\">");
        corpo.append("<h1 style=\"margin:0; color:#fff; font-size:20px;\">✅ Serviço Concluído!</h1>");
        corpo.append("<p style=\"margin:6px 0 0; color:#a7f3d0; font-size:13px;\">Piston & Wood — Sistema de Gestão</p>");
        corpo.append("</div>");

        // Saudação
        corpo.append("<div style=\"padding:32px;\">");
        corpo.append("<p style=\"color:#374151; font-size:15px;\">Olá, <strong>").append(os.getCliente().getNome()).append("</strong>!</p>");
        corpo.append("<p style=\"color:#6b7280; font-size:14px; margin-top:4px;\">Seu serviço foi concluído. Segue o resumo completo:</p>");

        // Detalhes
        corpo.append("<table style=\"width:100%; border-collapse:collapse; margin-top:20px;\">");
        appendRow(corpo, "Número da OS", "#" + os.getId());
        appendRow(corpo, "Categoria", os.getServicoCategoria() != null ? os.getServicoCategoria().getDescricao() : "—");
        appendRow(corpo, "Data de Abertura", os.getData() != null ? os.getData().toString().replace("T", " ").substring(0, 16) : "—");
        appendRow(corpo, "Mão de Obra", currency.format(os.getMaoDeObraValor()));
        appendRow(corpo, "Peças / Insumos", currency.format(os.getItensEstoqueTotal()));
        appendRowDestaque(corpo, "Valor Total", currency.format(os.getValorTotal() != null ? os.getValorTotal() : 0));
        corpo.append("</table>");

        // Descrição
        corpo.append("<div style=\"margin-top:24px; padding:16px; background:#f0fdf4; border-left:4px solid #059669; border-radius:4px;\">");
        corpo.append("<strong style=\"color:#065f46;\">Descrição do Serviço:</strong><br/>");
        corpo.append("<p style=\"margin:8px 0 0; color:#374151;\">").append(os.getDescricaoServico()).append("</p>");
        corpo.append("</div>");

        // Peças utilizadas
        if (!os.getItensEstoqueUtilizados().isEmpty()) {
            corpo.append("<div style=\"margin-top:28px;\">");
            corpo.append("<strong style=\"color:#111827; font-size:14px;\">Peças / Insumos Utilizados:</strong>");
            corpo.append("<table style=\"width:100%; border-collapse:collapse; margin-top:12px; font-size:13px;\">");
            corpo.append("<thead><tr style=\"background:#f3f4f6;\">");
            corpo.append("<th style=\"text-align:left; padding:8px 12px; border:1px solid #e5e7eb;\">Produto</th>");
            corpo.append("<th style=\"text-align:center; padding:8px 12px; border:1px solid #e5e7eb;\">Qtd</th>");
            corpo.append("<th style=\"text-align:right; padding:8px 12px; border:1px solid #e5e7eb;\">Preço Unit.</th>");
            corpo.append("<th style=\"text-align:right; padding:8px 12px; border:1px solid #e5e7eb;\">Subtotal</th>");
            corpo.append("</tr></thead><tbody>");
            for (OSItem item : os.getItensEstoqueUtilizados()) {
                double preco = item.getPrecoUnitario() != null ? item.getPrecoUnitario() : 0;
                double subtotal = item.getQuantidade() * preco;
                corpo.append("<tr>");
                corpo.append("<td style=\"padding:8px 12px; border:1px solid #e5e7eb;\">").append(item.getProduto().getNome()).append("</td>");
                corpo.append("<td style=\"padding:8px 12px; border:1px solid #e5e7eb; text-align:center;\">").append(item.getQuantidade()).append("</td>");
                corpo.append("<td style=\"padding:8px 12px; border:1px solid #e5e7eb; text-align:right;\">").append(currency.format(preco)).append("</td>");
                corpo.append("<td style=\"padding:8px 12px; border:1px solid #e5e7eb; text-align:right;\">").append(currency.format(subtotal)).append("</td>");
                corpo.append("</tr>");
            }
            corpo.append("</tbody></table></div>");
        }

        corpo.append("<p style=\"margin-top:24px; color:#6b7280; font-size:13px;\">Obrigado por confiar na Piston & Wood! Esperamos vê-lo novamente em breve.</p>");
        corpo.append("</div>");

        // Rodapé
        rodape(corpo);
        corpo.append("</div></body></html>");

        enviar(emailCliente, assunto, corpo.toString());
    }

    private void appendRow(StringBuilder sb, String label, String value) {
        sb.append("<tr style=\"border-bottom:1px solid #f3f4f6;\">");
        sb.append("<td style=\"padding:10px 4px; font-weight:600; color:#6b7280; width:40%; font-size:13px;\">").append(label).append("</td>");
        sb.append("<td style=\"padding:10px 4px; color:#111827; font-size:13px;\">").append(value).append("</td>");
        sb.append("</tr>");
    }

    private void appendRowDestaque(StringBuilder sb, String label, String value) {
        sb.append("<tr style=\"background:#f0fdf4;\">");
        sb.append("<td style=\"padding:12px 4px; font-weight:700; color:#065f46; font-size:14px;\">").append(label).append("</td>");
        sb.append("<td style=\"padding:12px 4px; color:#059669; font-size:14px; font-weight:700;\">").append(value).append("</td>");
        sb.append("</tr>");
    }

    private void rodape(StringBuilder sb) {
        sb.append("<div style=\"padding:16px 32px; background:#f9fafb; border-top:1px solid #e5e7eb;\">");
        sb.append("<p style=\"margin:0; font-size:12px; color:#9ca3af;\">Esta mensagem foi gerada automaticamente pelo sistema ERP <strong>Piston &amp; Wood</strong>. Por favor, não responda este email.</p>");
        sb.append("</div>");
    }

    private void enviar(String destinatario, String assunto, String htmlCorpo) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(remetente, "Piston & Wood ERP");
            helper.setTo(destinatario);
            helper.setSubject(assunto);
            helper.setText(htmlCorpo, true);
            mailSender.send(message);
            System.out.println("[EmailService] Email enviado para: " + destinatario + " | Assunto: " + assunto);
        } catch (Exception e) {
            System.err.println("[EmailService] Falha ao enviar email para " + destinatario + ": " + e.getMessage());
        }
    }
}
