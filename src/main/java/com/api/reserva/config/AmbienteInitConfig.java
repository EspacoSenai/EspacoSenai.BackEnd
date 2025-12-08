package com.api.reserva.config;

import com.api.reserva.entity.Ambiente;
import com.api.reserva.entity.Catalogo;
import com.api.reserva.enums.Aprovacao;
import com.api.reserva.enums.DiaSemana;
import com.api.reserva.enums.Disponibilidade;
import com.api.reserva.repository.AmbienteRepository;
import com.api.reserva.repository.CatalogoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalTime;

@Configuration
public class AmbienteInitConfig implements CommandLineRunner {

    @Autowired
    private AmbienteRepository ambienteRepository;

    @Autowired
    private CatalogoRepository catalogoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Criar ambientes
        criarAmbienteSeNaoExistir("Quadra", "Quadra de esportes");
        criarAmbienteSeNaoExistir("Auditório", "Auditório para apresentações");

        // Criar recursos (ambientes sem horário específico, apenas inventário)
        criarRecursoSeNaoExistir("PS5", "Console PlayStation 5");
        criarRecursoSeNaoExistir("PC1", "Computador de mesa 1 da Biblioteca");
        criarRecursoSeNaoExistir("PC2", "Computador de mesa 2 da Biblioteca");
        criarRecursoSeNaoExistir("PC2", "Computador de mesa 2 da Biblioteca");
        criarRecursoSeNaoExistir("PC3", "Computador de mesa 3 da Biblioteca");
        criarRecursoSeNaoExistir("PC4", "Computador de mesa 4 da Biblioteca");
        criarRecursoSeNaoExistir("PC5", "Computador de mesa 5 da Biblioteca");

        System.out.println("✓ Ambientes e recursos inicializados com sucesso!");
    }

    /**
     * Cria um ambiente se não existir
     *
     * @param nome Nome do ambiente
     * @param descricao Descrição do ambiente
     */
    private void criarAmbienteSeNaoExistir(String nome, String descricao) {
        if (ambienteRepository.findByNome(nome) == null) {
            Ambiente ambiente = new Ambiente();
            ambiente.setNome(nome);
            ambiente.setDescricao(descricao);
            ambiente.setDisponibilidade(Disponibilidade.DISPONIVEL);
            ambiente.setAprovacao(Aprovacao.MANUAL);
            ambiente.setEmUso(false);
            ambiente.setRecurso(false);

            ambienteRepository.save(ambiente);

            // Criar catálogos padrão para o ambiente (um para cada dia da semana)
            criarCatalogosPadrao(ambiente);

            System.out.println("✓ Ambiente criado: " + nome);
        } else {
            System.out.println("ℹ️ Ambiente já existe: " + nome);
        }
    }

    /**
     * Cria um recurso se não existir
     *
     * @param nome Nome do recurso
     * @param descricao Descrição do recurso
     */
    private void criarRecursoSeNaoExistir(String nome, String descricao) {
        if (ambienteRepository.findByNome(nome) == null) {
            Ambiente recursoAmb = new Ambiente();
            recursoAmb.setNome(nome);
            recursoAmb.setDescricao(descricao);
            recursoAmb.setDisponibilidade(Disponibilidade.DISPONIVEL);
            recursoAmb.setAprovacao(Aprovacao.MANUAL);
            recursoAmb.setEmUso(false);
            recursoAmb.setRecurso(true);

            ambienteRepository.save(recursoAmb);

            // Criar catálogos padrão para o recurso
            criarCatalogosPadrao(recursoAmb);

            System.out.println("✓ Recurso criado: " + nome);
        } else {
            System.out.println("ℹ️ Recurso já existe: " + nome);
        }
    }

    /**
     * Cria catálogos padrão para o ambiente/recurso
     * com horário de funcionamento padrão (8h às 22h) para cada dia da semana
     *
     * @param ambiente Ambiente para criar os catálogos
     */
    private void criarCatalogosPadrao(Ambiente ambiente) {
        // Verificar se já existem catálogos para este ambiente
        if (catalogoRepository.findByAmbienteId(ambiente.getId()).isEmpty()) {
            // Criar um catálogo para cada dia da semana
            for (DiaSemana dia : DiaSemana.values()) {
                Catalogo catalogo = new Catalogo();
                catalogo.setAmbiente(ambiente);
                catalogo.setDiaSemana(dia);
                catalogo.setHoraInicio(LocalTime.of(3, 0));
                catalogo.setHoraFim(LocalTime.of(22, 0));
                catalogo.setDisponibilidade(Disponibilidade.DISPONIVEL);

                catalogoRepository.save(catalogo);
            }
            System.out.println("  └─ Catálogos padrão criados para: " + ambiente.getNome());
        }
    }
}


