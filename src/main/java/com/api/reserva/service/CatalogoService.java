package com.api.reserva.service;

import com.api.reserva.dto.CatalogoDTO;
import com.api.reserva.dto.CatalogoReferenciaDTO;
import com.api.reserva.entity.Ambiente;
import com.api.reserva.entity.Catalogo;
import com.api.reserva.entity.Reserva;
import com.api.reserva.enums.StatusReserva;
import com.api.reserva.exception.SemPermissaoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.*;
import com.api.reserva.util.MetodosAuth;
import com.api.reserva.util.ValidacaoDatasEHorarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CatalogoService {
    @Autowired
    public CatalogoRepository catalogoRepository;
    @Autowired
    public AmbienteRepository ambienteRepository;

    @Autowired
    public HorarioRepository horarioRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ReservaRepository reservaRepository;

    public List<CatalogoReferenciaDTO> buscar() {
        return catalogoRepository.findAll().stream()
                .map(CatalogoReferenciaDTO::new)
                .toList();
    }

    public CatalogoReferenciaDTO buscar(Long id) {
        return new CatalogoReferenciaDTO(catalogoRepository.findById(id).orElseThrow(SemResultadosException::new));
    }

    @Transactional
    public void salvar(Long ambienteId, Set<CatalogoDTO> catalogosDTO, Authentication authentication) {
        Ambiente ambiente = ambienteRepository.findById(ambienteId).orElseThrow(() ->
                new SemResultadosException("vinculação de Ambiente."));

        if (!MetodosAuth.extrairRole(authentication).contains("SCOPE_ADMIN") ||
                !ambiente.getResponsaveis().contains(usuarioRepository.findById(MetodosAuth.extrairId(authentication))
                        .orElseThrow())) {
            throw new SemPermissaoException();
        }

        Set<Catalogo> catalogos = new HashSet<>(catalogoRepository.findCatalogoByAmbienteId(ambiente.getId()));


        catalogosDTO.forEach(catalogoDTO -> {
            ValidacaoDatasEHorarios.validarHorarios(catalogoDTO.getHoraInicio(), catalogoDTO.getHoraFim());
            for (Catalogo c : catalogos) {
                if (Objects.equals(catalogoDTO.getDiaSemana(), c.getDiaSemana())) {
                    ValidacaoDatasEHorarios.validarCatalogo(
                            catalogoDTO.getHoraInicio(), catalogoDTO.getHoraFim(),
                            c.getHoraInicio(), c.getHoraFim());
                }
            }
            Catalogo catalogo = new Catalogo();
            catalogo.setAmbiente(ambiente);
            catalogo.setDiaSemana(catalogoDTO.getDiaSemana());
            catalogo.setHoraInicio(catalogoDTO.getHoraInicio());
            catalogo.setHoraFim(catalogoDTO.getHoraFim());
            catalogo.setDisponibilidade(catalogoDTO.getDisponibilidade());
            catalogos.add(catalogo);
        });
        catalogoRepository.saveAll(catalogos);
    }

    public void atualizar(Long ambienteId, Set<CatalogoDTO> catalogosDTO, Authentication authentication) {
        Ambiente ambiente = ambienteRepository.findById(ambienteId).orElseThrow(() ->
                new SemResultadosException("ambiente"));

        if (!MetodosAuth.extrairRole(authentication).contains("SCOPE_ADMIN") ||
                !ambiente.getResponsaveis().contains(usuarioRepository.findById(MetodosAuth.extrairId(authentication))
                        .orElseThrow())) {
            throw new SemPermissaoException();
        }

        Set<Catalogo> catalogosExistentes = ambiente.getCatalogos();
        Set<Reserva> reservasExistentes = new HashSet<>(reservaRepository.findAll());
        Set<Catalogo> catalogosParaSalvar = new HashSet<>();

        catalogosDTO.forEach(catalogoDTO -> {
            ValidacaoDatasEHorarios.validarHorarios(catalogoDTO.getHoraInicio(), catalogoDTO.getHoraFim());
            Catalogo catalogoExistente = catalogosExistentes.stream()
                    .filter(c -> Objects.equals(c.getId(), catalogoDTO.getId()))
                    .findFirst()
                    .orElseThrow(() -> new SemResultadosException("catálogo não encontrado"));

            ValidacaoDatasEHorarios.validarCatalogo(catalogoDTO.getHoraInicio(), catalogoDTO.getHoraFim(),
                    catalogoExistente.getHoraInicio(), catalogoExistente.getHoraFim());

            boolean diaMudou = !Objects.equals(catalogoDTO.getDiaSemana(), catalogoExistente.getDiaSemana());
            boolean horarioMudou = !Objects.equals(catalogoDTO.getHoraInicio(), catalogoExistente.getHoraInicio()) ||
                    !Objects.equals(catalogoDTO.getHoraFim(), catalogoExistente.getHoraFim());
            boolean disponibilidadeMudou = !Objects.equals(catalogoDTO.getDisponibilidade(), catalogoExistente.getDisponibilidade());

            if (diaMudou || horarioMudou || disponibilidadeMudou) {
                Set<Reserva> reservasDoCatalogo = reservasExistentes.stream()
                        .filter(r -> r.getCatalogo().equals(catalogoExistente))
                        .collect(Collectors.toSet());
                reservasDoCatalogo.forEach(reserva -> {
                    reserva.setStatusReserva(StatusReserva.CANCELADA);
                    reserva.setMsgInterna("Cancelada automaticamente devido a alterações no catálogo do ambiente.");
                });
                reservaRepository.saveAll(reservasDoCatalogo);

                catalogoExistente.setDiaSemana(catalogoDTO.getDiaSemana());
                catalogoExistente.setHoraInicio(catalogoDTO.getHoraInicio());
                catalogoExistente.setHoraFim(catalogoDTO.getHoraFim());
                catalogoExistente.setDisponibilidade(catalogoDTO.getDisponibilidade());
                catalogosParaSalvar.add(catalogoExistente);
            }
        });

        catalogoRepository.saveAll(catalogosParaSalvar);
    }

    @Transactional
    public void deletar(Set<Long> catalogosIds, Authentication authentication) {
        for (Long id : catalogosIds) {
            Catalogo catalogo = catalogoRepository.findById(id).orElseThrow(
                    () -> new SemResultadosException("catálogo não encontrado"));

            Ambiente ambiente = catalogo.getAmbiente();
            if (!MetodosAuth.extrairRole(authentication).contains("SCOPE_ADMIN") ||
                    !ambiente.getResponsaveis().contains(usuarioRepository.findById(MetodosAuth.extrairId(authentication))
                            .orElseThrow())) {
                throw new SemPermissaoException();
            }

            // Excluir reservas vinculadas ao catálogo
            Set<Reserva> reservasDoCatalogo = reservaRepository.findAllByCatalogo_Id(id);
            reservaRepository.deleteAll(reservasDoCatalogo);

        }
        catalogoRepository.deleteAllById(catalogosIds);
    }
}
