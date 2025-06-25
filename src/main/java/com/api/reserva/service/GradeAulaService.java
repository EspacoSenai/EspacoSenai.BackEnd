package com.api.reserva.service;

import com.api.reserva.dto.GradeAulaDTO;
import com.api.reserva.entity.*;
import com.api.reserva.enums.UsuarioRole;
import com.api.reserva.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GradeAulaService {

    private final GradeAulaRepository gradeAulaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HorarioRepository horarioRepository;
    private final PeriodoRepository periodoRepository;
    private final DisciplinaRepository disciplinaRepository;

    public GradeAulaService(
            GradeAulaRepository gradeAulaRepository,
            UsuarioRepository usuarioRepository,
            HorarioRepository horarioRepository,
            PeriodoRepository periodoRepository,
            DisciplinaRepository disciplinaRepository
    ) {
        this.gradeAulaRepository = gradeAulaRepository;
        this.usuarioRepository = usuarioRepository;
        this.horarioRepository = horarioRepository;
        this.periodoRepository = periodoRepository;
        this.disciplinaRepository = disciplinaRepository;
    }

    public List<GradeAulaDTO> listar() {
        return gradeAulaRepository.findAll()
                .stream()
                .map(GradeAulaDTO::new)
                .collect(Collectors.toList());
    }

    public GradeAulaDTO buscarPorId(Long id) {
        GradeAula aula = gradeAulaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aula não encontrada"));
        return new GradeAulaDTO(aula);
    }

    @Transactional
    public GradeAulaDTO criar(GradeAulaDTO dto) {
        // Verifica conflito de aula
        gradeAulaRepository.findBySalaAndDiaAndHorario_Id(dto.getSala(), dto.getDia(), dto.getIdHorario())
                .ifPresent(a -> { throw new RuntimeException("Já existe uma aula para essa sala, dia e horário."); });

        Usuario professor = usuarioRepository.findById(dto.getIdProfessor())
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));

        if (professor.getRole() != UsuarioRole.PROFESSOR) {
            throw new RuntimeException("Apenas usuários com papel de PROFESSOR podem criar aulas.");
        }

        Horario horario = horarioRepository.findById(dto.getIdHorario())
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado"));

        Periodo periodo = periodoRepository.findById(dto.getIdPeriodo())
                .orElseThrow(() -> new EntityNotFoundException("Período não encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(dto.getIdDisciplina())
                .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada"));

        GradeAula aula = new GradeAula(dto, professor, disciplina, horario, periodo);
        aula = gradeAulaRepository.save(aula);
        return new GradeAulaDTO(aula);
    }

    @Transactional
    public GradeAulaDTO atualizar(Long id, GradeAulaDTO dto) {
        GradeAula aula = gradeAulaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aula não encontrada"));

        // Verifica conflito de aula (exceto a própria)
        gradeAulaRepository.findBySalaAndDiaAndHorario_Id(dto.getSala(), dto.getDia(), dto.getIdHorario())
                .filter(a -> !a.getId().equals(id))
                .ifPresent(a -> { throw new RuntimeException("Já existe uma aula para essa sala, dia e horário."); });

        Usuario professor = usuarioRepository.findById(dto.getIdProfessor())
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));

        if (professor.getRole() != UsuarioRole.PROFESSOR) {
            throw new RuntimeException("Apenas usuários com papel de PROFESSOR podem atualizar aulas.");
        }

        Horario horario = horarioRepository.findById(dto.getIdHorario())
                .orElseThrow(() -> new EntityNotFoundException("Horário não encontrado"));

        Periodo periodo = periodoRepository.findById(dto.getIdPeriodo())
                .orElseThrow(() -> new EntityNotFoundException("Período não encontrado"));

        Disciplina disciplina = disciplinaRepository.findById(dto.getIdDisciplina())
                .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada"));

        aula.setSala(dto.getSala());
        aula.setProfessor(professor);
        aula.setDisciplina(disciplina);
        aula.setHorario(horario);
        aula.setPeriodo(periodo);
        aula.setDia(dto.getDia());

        aula = gradeAulaRepository.save(aula);
        return new GradeAulaDTO(aula);
    }

    @Transactional
    public void deletar(Long id, Long idProfessor) {
        GradeAula aula = gradeAulaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aula não encontrada"));

        Usuario professor = usuarioRepository.findById(idProfessor)
                .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));

        if (professor.getRole() != UsuarioRole.PROFESSOR) {
            throw new RuntimeException("Apenas usuários com papel de PROFESSOR podem deletar aulas.");
        }

        gradeAulaRepository.delete(aula);
    }
}