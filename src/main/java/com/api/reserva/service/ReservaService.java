package com.api.reserva.service;

import com.api.reserva.dto.ReservaDTO;
import com.api.reserva.dto.ReservaReferenciaDTO;
import com.api.reserva.entity.Catalogo;
import com.api.reserva.entity.Reserva;
import com.api.reserva.enums.StatusReserva;
import com.api.reserva.exception.DataInvalidaException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.CatalogoRepository;
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
    private CatalogoRepository catalogoRepository;

    public List<ReservaReferenciaDTO> buscar() {
        return reservaRepository.findAll()
                .stream()
                .map(ReservaReferenciaDTO::new)
                .toList();
    }

    public ReservaReferenciaDTO buscar(Long id) {
        return new ReservaReferenciaDTO(reservaRepository.findById(id).orElseThrow(
                () -> new SemResultadosException()));
    }

    @Transactional
    public void salvar(ReservaDTO reservaDTO) {
        Reserva reserva = new Reserva();

//        reservaRepository.findAllByUsuario()
//
        reserva.setHost(usuarioRepository.findById(reservaDTO.getHostId()).orElseThrow(
                () -> new SemResultadosException("associação: Usuario")));

        Catalogo catalogo = catalogoRepository.findById(reservaDTO.getCatalogoId()).orElseThrow(
                () -> new SemResultadosException("associação: Catalogo"));


//
//        if (catalogo.getAgendamento() == Agendamento.PERIODO) {
//
//            Periodo periodo = periodoRepository.findById(catalogo.getPeriodo().getId()).orElseThrow(
//                    () -> new SemResultadosException("associação: Período"));
//            reserva.setHoraInicio(periodo.getHoraInicio());
//            reserva.setHoraFim(periodo.getHoraFim());
//        } else {
//
//            if (reservaDTO.getHoraInicio().isAfter(catalogo.getHorario().getHoraFim()) ||
//                    reservaDTO.getHoraInicio().equals(catalogo.getHorario().getHoraFim()) ||
//                    reservaDTO.getHoraInicio().isBefore(catalogo.getHorario().getHoraInicio()) ||
//                    reservaDTO.getHoraFim().isAfter(catalogo.getHorario().getHoraFim())) {
//                throw new HorarioInvalidoException();
//            }
//            reserva.setHoraInicio(reservaDTO.getHoraInicio());
//            reserva.setHoraFim(reservaDTO.getHoraFim());
//        }


        if (reservaDTO.getData().getDayOfWeek() != catalogo.getDiaSemana().getDayOfWeek()) {
            throw new DataInvalidaException();
        }

        reserva.setCatalogo(catalogo);
        reserva.setHoraInicio(reservaDTO.getHoraInicio());
        reserva.setHoraFim(reservaDTO.getHoraFim());
        reserva.setData(reservaDTO.getData());
        reserva.setStatusReserva(StatusReserva.PENDENTE);
        reserva.setMsgInterna(reservaDTO.getMsgInterna());

        reservaRepository.save(reserva);
    }

    public void atualizar(Long id, ReservaDTO reservaDTO) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(
                () -> new SemResultadosException("atualização"));
        reserva.setHost(usuarioRepository.findById(reservaDTO.getHostId()).orElseThrow(
                () -> new SemResultadosException("associação: Usuario")));

        Catalogo catalogo = catalogoRepository.findById(reservaDTO.getCatalogoId()).orElseThrow(
                () -> new SemResultadosException("associação: GradeAmbiente"));

//        if (catalogo.getAgendamento() == Agendamento.PERIODO) {
//
//            Periodo periodo = periodoRepository.findById(catalogo.getPeriodo().getId()).orElseThrow(
//                    () -> new SemResultadosException("associação: Período"));
//            reserva.setHoraInicio(periodo.getHoraInicio());
//            reserva.setHoraFim(periodo.getHoraFim());
//        } else {
//
//            if (reservaDTO.getHoraInicio().isAfter(catalogo.getHorario().getHoraFim()) ||
//                    reservaDTO.getHoraInicio().equals(catalogo.getHorario().getHoraFim()) ||
//                    reservaDTO.getHoraInicio().isBefore(catalogo.getHorario().getHoraInicio()) ||
//                    reservaDTO.getHoraFim().isAfter(catalogo.getHorario().getHoraFim())) {
//                throw new HorarioInvalidoException();
//            }
//            reserva.setHoraInicio(reservaDTO.getHoraInicio());
//            reserva.setHoraFim(reservaDTO.getHoraFim());
//        }


        if (reservaDTO.getData().getDayOfWeek() != catalogo.getDiaSemana().getDayOfWeek()) {
            throw new DataInvalidaException();
        }

        reserva.setCatalogo(catalogo);
        reserva.setHoraInicio(reservaDTO.getHoraInicio());
        reserva.setHoraFim(reservaDTO.getHoraFim());
        reserva.setData(reservaDTO.getData());
        reserva.setStatusReserva(StatusReserva.PENDENTE);
        reserva.setMsgInterna(reservaDTO.getMsgInterna());

        reservaRepository.save(reserva);
    }

    public void deletar(Long id) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(
                () -> new SemResultadosException("exclusão;"));
        reservaRepository.delete(reserva);
    }
}
