package com.api.reserva.service;

import com.api.reserva.dto.ReservaDTO;
import com.api.reserva.dto.ReservaReferenciaDTO;
import com.api.reserva.entity.GradeAmbiente;
import com.api.reserva.entity.Periodo;
import com.api.reserva.entity.Reserva;
import com.api.reserva.enums.Agendamento;
import com.api.reserva.enums.DiaSemana;
import com.api.reserva.enums.StatusReserva;
import com.api.reserva.exception.DataInvalidaException;
import com.api.reserva.exception.HorarioInvalidoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.GradeAmbienteRepository;
import com.api.reserva.repository.PeriodoRepository;
import com.api.reserva.repository.ReservaRepository;
import com.api.reserva.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservaService {
    @Autowired
    private ReservaRepository reservaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private GradeAmbienteRepository gradeAmbienteRepository;
    @Autowired
    private PeriodoRepository periodoRepository;

    public List<ReservaReferenciaDTO> listar() {
        return reservaRepository.findAll()
                .stream()
                .map(ReservaReferenciaDTO::new)
                .toList();
    }

    public ReservaReferenciaDTO listar(Long id) {
        return new ReservaReferenciaDTO(reservaRepository.findById(id).orElseThrow(
                () -> new SemResultadosException()));
    }

    @Transactional
    public void salvar(ReservaDTO reservaDTO) {
        Reserva reserva = new Reserva();

        reserva.setUsuario(usuarioRepository.findById(reservaDTO.getIdUsuario()).orElseThrow(
                () -> new SemResultadosException("associação: Usuario")));

        GradeAmbiente gradeAmbiente = gradeAmbienteRepository.findById(reservaDTO.getIdGradeAmbiente()).orElseThrow(
                () -> new SemResultadosException("associação: GradeAmbiente"));

        if (gradeAmbiente.getAgendamento() == Agendamento.PERIODO) {

            Periodo periodo = periodoRepository.findById(gradeAmbiente.getPeriodo().getId()).orElseThrow(
                    () -> new SemResultadosException("associação: Período"));
            reserva.setHoraInicio(periodo.getHoraInicio());
            reserva.setHoraFim(periodo.getHoraFim());
        } else {

            if (reservaDTO.getHoraInicio().isAfter(gradeAmbiente.getHorario().getHoraFim()) ||
                    reservaDTO.getHoraInicio().equals(gradeAmbiente.getHorario().getHoraFim()) ||
                    reservaDTO.getHoraInicio().isBefore(gradeAmbiente.getHorario().getHoraInicio()) ||
                    reservaDTO.getHoraFim().isAfter(gradeAmbiente.getHorario().getHoraFim())) {
                throw new HorarioInvalidoException();
            }
            reserva.setHoraInicio(reservaDTO.getHoraInicio());
            reserva.setHoraFim(reservaDTO.getHoraFim());
        }


        if(reservaDTO.getData().getDayOfWeek() != gradeAmbiente.getDiaSemana().getDayOfWeek()){
            throw new DataInvalidaException();
        }

        reserva.setData(reservaDTO.getData());
        reserva.setStatusReserva(StatusReserva.PENDENTE);
        reserva.setMsgInterna(reservaDTO.getMsgInterna());

        reservaRepository.save(reserva);
    }

    public void atualizar(Long id, ReservaDTO reservaDTO) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(
                () -> new SemResultadosException("atualização"));
        reserva.setUsuario(usuarioRepository.findById(reservaDTO.getIdUsuario()).orElseThrow(
                () -> new SemResultadosException("associação: Usuario")));

        GradeAmbiente gradeAmbiente = gradeAmbienteRepository.findById(reservaDTO.getIdGradeAmbiente()).orElseThrow(
                () -> new SemResultadosException("associação: GradeAmbiente"));

        if (gradeAmbiente.getAgendamento() == Agendamento.PERIODO) {

            Periodo periodo = periodoRepository.findById(gradeAmbiente.getPeriodo().getId()).orElseThrow(
                    () -> new SemResultadosException("associação: Período"));
            reserva.setHoraInicio(periodo.getHoraInicio());
            reserva.setHoraFim(periodo.getHoraFim());
        } else {

            if (reservaDTO.getHoraInicio().isAfter(gradeAmbiente.getHorario().getHoraFim()) ||
                    reservaDTO.getHoraInicio().equals(gradeAmbiente.getHorario().getHoraFim()) ||
                    reservaDTO.getHoraInicio().isBefore(gradeAmbiente.getHorario().getHoraInicio()) ||
                    reservaDTO.getHoraFim().isAfter(gradeAmbiente.getHorario().getHoraFim())) {
                throw new HorarioInvalidoException();
            }
            reserva.setHoraInicio(reservaDTO.getHoraInicio());
            reserva.setHoraFim(reservaDTO.getHoraFim());
        }


        if(reservaDTO.getData().getDayOfWeek() != gradeAmbiente.getDiaSemana().getDayOfWeek()){
            throw new DataInvalidaException();
        }

        reserva.setData(reservaDTO.getData());
        reserva.setStatusReserva(StatusReserva.PENDENTE);
        reserva.setMsgInterna(reservaDTO.getMsgInterna());

        reservaRepository.save(reserva);
    }

    public void excluir(Long id) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(
                () -> new SemResultadosException("exclusão;"));
        reservaRepository.delete(reserva);
    }
}
