package com.api.reserva.service;

import com.api.reserva.dto.Pin;
import com.api.reserva.dto.Reserva3dDTO;
import com.api.reserva.dto.ReservaImpressoraReferenciaDTO;
import com.api.reserva.dto.Temperatura;
import com.api.reserva.entity.*;
import com.api.reserva.enums.Disponibilidade;
import com.api.reserva.enums.StatusReserva3D;
import com.api.reserva.exception.DataInvalidaException;
import com.api.reserva.exception.HorarioInvalidoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.*;
import com.api.reserva.util.CodigoUtil;
import com.api.reserva.util.MetodosAuth;
import com.api.reserva.util.ValidacaoDatasEHorarios;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Set;

import static com.api.reserva.enums.StatusReserva3D.DESLIGADA;

@Service
public class ReservaImpressoraService {
    @Autowired
    private ReservaImpressoraRepository impressoraRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CatalogoRepository catalogoRepository;
    @Autowired
    private ReservaService reservaService;
    @Autowired
    private ReservaImpressoraRepository reservaImpressoraRepository;


    public List<ReservaImpressoraReferenciaDTO> buscar() {
        return impressoraRepository.findAll()
                .stream()
                .map(ReservaImpressoraReferenciaDTO::new)
                .toList();
    }

    public ReservaImpressoraReferenciaDTO buscar(Long id) {
        return new ReservaImpressoraReferenciaDTO(impressoraRepository.findById(id).orElseThrow(
                SemResultadosException::new));
    }

    @Transactional
    public void salvar(ReservaImpressoraReferenciaDTO reserva, Authentication authentication){
        validarDadosReserva(reserva);

        Usuario host = usuarioRepository.findById(MetodosAuth.extrairId(authentication)).orElseThrow(
                () -> new SemResultadosException("Usuário"));



        LocalDate dataDaReserva = reserva.getData();
        LocalTime inicioReserva = reserva.getHoraInicio();
        LocalTime fimReserva = reserva.getHoraFim();

        // Validar se a reserva é futura ou para hoje com 15 minutos de antecedência
        LocalTime agora = LocalTime.now();
        LocalTime minimoDeTempo = agora.plusMinutes(15);


        if (dataDaReserva.equals(LocalDate.now())) {
            // Valida se o horário fim já não passou
            if (fimReserva.isBefore(agora) || fimReserva.equals(agora)) {
                throw new HorarioInvalidoException("A reserva deve ser para um horário futuro.");
            }
            // Valida se tem 15 minutos de antecedência antes do INÍCIO
            if (inicioReserva.isBefore(minimoDeTempo)) {
                throw new HorarioInvalidoException("A reserva deve ser feita com no mínimo 15 minutos de antecedência.");
            }
        }
        validarSobreposicaoHorarios(dataDaReserva, inicioReserva, fimReserva);

        // Criar reserva
        ReservaImpressora reservaImpressora = new ReservaImpressora(
                host,
                reserva.getData(),
                reserva.getHoraInicio(),
                reserva.getHoraFim(),
                LocalDateTime.now(),
                geradorDePin()
        );

        reservaImpressora.setStatusReserva(DESLIGADA);
        impressoraRepository.save(reservaImpressora);

    }



    private void validarDadosReserva(ReservaImpressoraReferenciaDTO reservaDTO) {
        if (reservaDTO.getData().isBefore(LocalDate.now())) {
            throw new DataInvalidaException("A data da reserva deve ser futura ou hoje");
        }

        // Validar horários
        ValidacaoDatasEHorarios.validarHorarios(reservaDTO.getHoraInicio(), reservaDTO.getHoraFim());
        ValidacaoDatasEHorarios.atendeDuracaoMinima(reservaDTO.getHoraInicio(), reservaDTO.getHoraFim());
    }



    public Integer geradorDePin() {
        Integer maxPin = impressoraRepository.findMaxPin();
        if (maxPin == null) {
            maxPin = 0;
        }

        int nextPin = maxPin + 1;

        return nextPin;
    }

    public void atualizarStatusPeloPin(Pin pin) {

        ReservaImpressora reserva = impressoraRepository.findByPin(pin.pin());

        if (reserva == null) throw new RuntimeException("Reserva não encontrada para o PIN: " + pin.pin());

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime inicio = LocalDateTime.of(reserva.getData(), reserva.getHoraInicio());
        LocalDateTime fim = LocalDateTime.of(reserva.getData(), reserva.getHoraFim());

        LocalDateTime limiteMinimo = inicio.minusMinutes(15);

        if (agora.isBefore(limiteMinimo)) throw new HorarioInvalidoException("A maquina só será liberada 15 minutos antes do inicio da reserva.");

        if (agora.isAfter(fim)) throw new HorarioInvalidoException("A maquina não pode ser liberada após a data da reserva.");

        if (reserva.getStatusReserva() == StatusReserva3D.DESLIGADA) {
            reserva.setStatusReserva(StatusReserva3D.LIGADA);
        }

        impressoraRepository.save(reserva);

    }

    private boolean horariosSesobrepem(LocalTime inicio1, LocalTime fim1, LocalTime inicio2, LocalTime fim2) {
        return inicio1.isBefore(fim2) && fim1.isAfter(inicio2);
    }

    private void validarSobreposicaoHorarios(LocalDate data, LocalTime inicioReserva, LocalTime fimReserva) {
        java.util.Set<ReservaImpressora> todas = new java.util.HashSet<>(impressoraRepository.findAll());

        for (ReservaImpressora existente : todas) {
            if (data.equals(existente.getData())) {
                if (horariosSesobrepem(inicioReserva, fimReserva, existente.getHoraInicio(), existente.getHoraFim())) {
                    throw new HorarioInvalidoException("Já existe uma reserva de impressora neste horário para este dia.");
                }
            }
        }
    }

    public void atualizarTemperatura(Temperatura temperatura){
        ReservaImpressora reserva = impressoraRepository.findById(temperatura.id()).orElseThrow(
                () -> new SemResultadosException("Maquina não achada."));
        if(reserva.getStatusReserva() != StatusReserva3D.LIGADA){}

        reserva.setTemperatura(temperatura.temperatura());
        impressoraRepository.save(reserva);
    }

    public boolean desligarMaquina(){
        List<ReservaImpressora> reservaImpressora = impressoraRepository.findAll();
        for(ReservaImpressora reservaImpressora1 : reservaImpressora){
            LocalDateTime agora = LocalDateTime.of( reservaImpressora1.getData(),reservaImpressora1.getHoraFim());
            if(reservaImpressora1.getStatusReserva() == StatusReserva3D.LIGADA && agora.isAfter(LocalDateTime.now())){
                reservaImpressora1.getTemperatura();
                if(reservaImpressora1.getTemperatura() < 30){
                    reservaImpressora1.setStatusReserva(StatusReserva3D.DESLIGADA);
                    impressoraRepository.save(reservaImpressora1);
                    return true;
                }
            }
        }
        return false;
    }




}