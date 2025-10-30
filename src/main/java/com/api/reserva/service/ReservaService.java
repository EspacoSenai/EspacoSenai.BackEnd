package com.api.reserva.service;

import com.api.reserva.dto.ReservaDTO;
import com.api.reserva.dto.ReservaReferenciaDTO;
import com.api.reserva.entity.Catalogo;
import com.api.reserva.entity.Reserva;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.Disponibilidade;
import com.api.reserva.enums.StatusReserva;
import com.api.reserva.exception.DataInvalidaException;
import com.api.reserva.exception.HorarioInvalidoException;
import com.api.reserva.exception.SemPermissaoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.CatalogoRepository;
import com.api.reserva.repository.ReservaRepository;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.util.MetodosAuth;
import com.api.reserva.util.ValidacaoDatasEHorarios;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public List<ReservaReferenciaDTO> buscarPorUsuario(Authentication authentication) {
        Long usuarioId = MetodosAuth.extrairId(authentication);

        // Buscar reservas onde o usuário é host
        Set<Reserva> comoHost = reservaRepository.findAllByHost_Id(usuarioId);

        // Buscar reservas onde o usuário é membro/participante
        Set<Reserva> comoMembro = reservaRepository.findAllByMembros_Id(usuarioId);

        // Unir resultados (evita duplicatas quando o usuário é host e membro de uma mesma reserva)
        Set<Reserva> todas = new HashSet<>();
        if (comoHost != null) {
            todas.addAll(comoHost);
        }
        if (comoMembro != null) {
            todas.addAll(comoMembro);
        }

        return todas.stream()
                .map(ReservaReferenciaDTO::new)
                .toList();
    }

    public ReservaReferenciaDTO buscar(Long id) {
        return new ReservaReferenciaDTO(reservaRepository.findById(id).orElseThrow(
                () -> new SemResultadosException()));
    }

    @Transactional
    public void salvar(ReservaDTO reservaDTO, Authentication authentication) {
        // Validações básicas
        validarDadosReserva(reservaDTO);

        // Buscar entidades necessárias
        Usuario host = usuarioRepository.findById(reservaDTO.getHostId()).orElseThrow(
                () -> new SemResultadosException("Usuário não encontrado"));

        Catalogo catalogo = catalogoRepository.findById(reservaDTO.getCatalogoId()).orElseThrow(
                () -> new SemResultadosException("Catálogo não encontrado"));

        // Validar se o usuário logado pode criar reserva para o host informado
        Long usuarioLogadoId = MetodosAuth.extrairId(authentication);
        Set<String> role = MetodosAuth.extrairRole(authentication);

        if (!usuarioLogadoId.equals(reservaDTO.getHostId()) &&
            (role == null || !role.contains("SCOPE_ADMIN") && !role.contains("SCOPE_COORDENADOR"))) {
            throw new SemPermissaoException();
        }

        // Validar catálogo
        validarCatalogo(catalogo, reservaDTO);

        // Validar sobreposição de horários
        validarSobreposicaoHorarios(catalogo, reservaDTO);

        // Criar reserva
        Reserva reserva = new Reserva();
        reserva.setHost(host);
        reserva.setCatalogo(catalogo);
        reserva.setData(reservaDTO.getData());
        reserva.setHoraInicio(reservaDTO.getHoraInicio());
        reserva.setHoraFim(reservaDTO.getHoraFim());
        reserva.setStatusReserva(StatusReserva.PENDENTE);
        reserva.setMsgUsuario(reservaDTO.getMsgUsuario());
        reserva.setDataHoraSolicitacao(LocalDateTime.now());

        reserva = reservaRepository.save(reserva);

        // Adicionar convidados se houver
        if (reservaDTO.getConvidadosIds() != null && !reservaDTO.getConvidadosIds().isEmpty()) {
            adicionarConvidados(reserva, reservaDTO.getConvidadosIds());
        }
    }

    @Transactional
    public void atualizar(Long id, ReservaDTO reservaDTO, Authentication authentication) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(
                () -> new SemResultadosException("Reserva não encontrada"));

        // Verificar permissão
        Long usuarioLogadoId = MetodosAuth.extrairId(authentication);
        Set<String> role = MetodosAuth.extrairRole(authentication);

        if (!usuarioLogadoId.equals(reserva.getHost().getId()) &&
            (role == null || !role.contains("SCOPE_ADMIN") && !role.contains("SCOPE_COORDENADOR"))) {
            throw new SemPermissaoException();
        }

        // Validações básicas
        validarDadosReserva(reservaDTO);

        // Buscar catálogo se mudou
        Catalogo catalogo = null;
        if (!reservaDTO.getCatalogoId().equals(reserva.getCatalogo().getId())) {
            catalogo = catalogoRepository.findById(reservaDTO.getCatalogoId()).orElseThrow(
                    () -> new SemResultadosException("Catálogo não encontrado"));
            validarCatalogo(catalogo, reservaDTO);
        } else {
            catalogo = reserva.getCatalogo();
        }

        // Validar sobreposição excluindo a própria reserva
        validarSobreposicaoHorariosExcluindoReserva(catalogo, reservaDTO, id);

        // Atualizar dados
        reserva.setCatalogo(catalogo);
        reserva.setData(reservaDTO.getData());
        reserva.setHoraInicio(reservaDTO.getHoraInicio());
        reserva.setHoraFim(reservaDTO.getHoraFim());
        reserva.setMsgUsuario(reservaDTO.getMsgUsuario());

//        // Atualizar convidados
//        atualizarConvidados(reserva, reservaDTO.getConvidadosIds());

        reservaRepository.save(reserva);
    }

    @Transactional
    public void deletar(Long id, Authentication authentication) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(
                () -> new SemResultadosException("Reserva não encontrada"));

        // Verificar permissão
        Long usuarioLogadoId = MetodosAuth.extrairId(authentication);
        Set<String> role = MetodosAuth.extrairRole(authentication);

        if (!usuarioLogadoId.equals(reserva.getHost().getId()) &&
            (role == null || !role.contains("SCOPE_ADMIN") && !role.contains("SCOPE_COORDENADOR"))) {
            throw new SemPermissaoException();
        }

        reservaRepository.delete(reserva);
    }

    @Transactional
    public void aprovar(Long id, Authentication authentication) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(
                () -> new SemResultadosException("Reserva não encontrada"));

        Set<String> role = MetodosAuth.extrairRole(authentication);
        if (role == null || !role.contains("SCOPE_ADMIN") && !role.contains("SCOPE_COORDENADOR")) {
            throw new SemPermissaoException();
        }

        reserva.setStatusReserva(StatusReserva.APROVADA);
        reservaRepository.save(reserva);
    }

    @Transactional
    public void rejeitar(Long id, String motivo, Authentication authentication) {
        Reserva reserva = reservaRepository.findById(id).orElseThrow(
                () -> new SemResultadosException("Reserva não encontrada"));

        Set<String> role = MetodosAuth.extrairRole(authentication);
        if (role == null || !role.contains("SCOPE_ADMIN") && !role.contains("SCOPE_COORDENADOR")) {
            throw new SemPermissaoException();
        }

        reserva.setStatusReserva(StatusReserva.NEGADA);
        reserva.setMsgInterna(motivo);
        reservaRepository.save(reserva);
    }

    private void validarDadosReserva(ReservaDTO reservaDTO) {
        // Validar se a data é futura
        if (reservaDTO.getData().isBefore(LocalDate.now())) {
            throw new DataInvalidaException("A data da reserva deve ser futura");
        }

        // Validar horários
        ValidacaoDatasEHorarios.validarHorarios(reservaDTO.getHoraInicio(), reservaDTO.getHoraFim());
        ValidacaoDatasEHorarios.atendeDuracaoMinima(reservaDTO.getHoraInicio(), reservaDTO.getHoraFim());
    }

    private void validarCatalogo(Catalogo catalogo, ReservaDTO reservaDTO) {
        // Verificar se o catálogo está disponível
        if (catalogo.getDisponibilidade() != Disponibilidade.DISPONIVEL) {
            throw new HorarioInvalidoException("O catálogo não está disponível para reservas");
        }

        // Verificar se o dia da semana coincide
        if (reservaDTO.getData().getDayOfWeek() != catalogo.getDiaSemana().getDayOfWeek()) {
            throw new DataInvalidaException("A data não corresponde ao dia da semana do catálogo");
        }

        // Verificar se os horários estão dentro do limite do catálogo
        if (reservaDTO.getHoraInicio().isBefore(catalogo.getHoraInicio()) ||
            reservaDTO.getHoraFim().isAfter(catalogo.getHoraFim())) {
            throw new HorarioInvalidoException("Os horários da reserva devem estar dentro do horário do catálogo");
        }
    }

    private void validarSobreposicaoHorarios(Catalogo catalogo, ReservaDTO reservaDTO) {
        List<Reserva> reservasExistentes = reservaRepository.findAllByCatalogo_Id(catalogo.getId())
                .stream()
                .filter(r -> r.getData().equals(reservaDTO.getData()) &&
                           r.getStatusReserva() != StatusReserva.CANCELADA &&
                           r.getStatusReserva() != StatusReserva.NEGADA)
                .toList();

        for (Reserva reservaExistente : reservasExistentes) {
            if (horariosSesobrepem(
                    reservaDTO.getHoraInicio(), reservaDTO.getHoraFim(),
                    reservaExistente.getHoraInicio(), reservaExistente.getHoraFim())) {
                throw new HorarioInvalidoException("Já existe uma reserva neste horário");
            }
        }
    }

    private void validarSobreposicaoHorariosExcluindoReserva(Catalogo catalogo, ReservaDTO reservaDTO, Long reservaId) {
        List<Reserva> reservasExistentes = reservaRepository.findAllByCatalogo_Id(catalogo.getId())
                .stream()
                .filter(r -> !r.getId().equals(reservaId) &&
                           r.getData().equals(reservaDTO.getData()) &&
                           r.getStatusReserva() != StatusReserva.CANCELADA &&
                           r.getStatusReserva() != StatusReserva.NEGADA)
                .toList();

        for (Reserva reservaExistente : reservasExistentes) {
            if (horariosSesobrepem(
                    reservaDTO.getHoraInicio(), reservaDTO.getHoraFim(),
                    reservaExistente.getHoraInicio(), reservaExistente.getHoraFim())) {
                throw new HorarioInvalidoException("Já existe uma reserva neste horário");
            }
        }
    }

    private boolean horariosSesobrepem(LocalTime inicio1, LocalTime fim1, LocalTime inicio2, LocalTime fim2) {
        return inicio1.isBefore(fim2) && fim1.isAfter(inicio2);
    }

    private void adicionarConvidados(Reserva reserva, Set<Long> convidadosIds) {
//        Set<Usuario> convidados = new HashSet<>();
//
//        for (Long convidadoId : convidadosIds) {
//            Usuario convidado = usuarioRepository.findById(convidadoId).orElseThrow(
//                    () -> new SemResultadosException("Convidado não encontrado: " + convidadoId));
//
//            Usuario reservaConvidado = new ReservaConvidados();
//            reservaConvidado.setReserva(reserva);
//            reservaConvidado.setConvidado(convidado);
//            convidados.add(reservaConvidado);
//        }
//
//        reserva.setParticipantes(convidados);
//        reservaConvidadosRepository.saveAll(convidados);
//    }
//
//    private void atualizarConvidados(Reserva reserva, Set<Long> novosConvidadosIds) {
//        // Remover convidados existentes
//        if (reserva.getParticipantes() != null) {
//            reservaConvidadosRepository.deleteAll(reserva.getParticipantes());
//            reserva.getParticipantes().clear();
//        }
//
//        // Adicionar novos convidados
//        if (novosConvidadosIds != null && !novosConvidadosIds.isEmpty()) {
//            adicionarConvidados(reserva, novosConvidadosIds);
//        }
    }
}
