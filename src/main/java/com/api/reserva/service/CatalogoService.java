package com.api.reserva.service;

import com.api.reserva.dto.CatalogoDTO;
import com.api.reserva.dto.CatalogoReferenciaDTO;
import com.api.reserva.entity.Catalogo;
import com.api.reserva.enums.Agendamento;
import com.api.reserva.exception.DadoInvalidoException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.AmbienteRepository;
import com.api.reserva.repository.CatalogoRepository;
import com.api.reserva.repository.HorarioRepository;
import com.api.reserva.util.ValidacaoDatasEHorarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class CatalogoService {
    @Autowired
    public CatalogoRepository catalogoRepository;
    @Autowired
    public AmbienteRepository ambienteRepository;

    @Autowired
    public HorarioRepository horarioRepository;

    public List<CatalogoReferenciaDTO> buscar() {
        return catalogoRepository.findAll().stream()
                .map(CatalogoReferenciaDTO::new)
                .toList();
    }

    public CatalogoReferenciaDTO buscar(Long id) {
        return new CatalogoReferenciaDTO(catalogoRepository.findById(id).orElseThrow(SemResultadosException::new));
    }

    @Transactional
    public void salvar(CatalogoDTO catalogoDTO) {
        Catalogo catalogo = new Catalogo();

        if (ValidacaoDatasEHorarios.validarHorarios(catalogoDTO.getHoraInicio(), catalogoDTO.getHoraFim())) {
            catalogo.setHoraInicio(catalogoDTO.getHoraInicio());
            catalogo.setHoraFim(catalogoDTO.getHoraFim());
        }

        catalogo.setAmbiente(ambienteRepository.findById(catalogoDTO.getIdAmbiente()).orElseThrow(() ->
                new SemResultadosException("vinculação de Ambiente.")));

        catalogo.setDiaSemana(catalogoDTO.getDiaSemana());
        catalogo.setDisponibilidade(catalogoDTO.getDisponibilidade());

        catalogoRepository.save(catalogo);
    }

//    public void atualizar(Long id, CatalogoDTO catalogoDTO) {
//        Catalogo catalogo = catalogoRepository.findById(id).orElseThrow(()
//                -> new SemResultadosException("atualização"));
//
//        catalogo.setAmbiente(ambienteRepository.findById(catalogoDTO.getIdAmbiente()).orElseThrow(() ->
//                new SemResultadosException("vinculação de Ambiente.")));
//
//        if (Objects.equals(catalogoDTO.getAgendamento(), Agendamento.HORARIO)
//                && catalogoDTO.getIdPeriodo() == null) {
//            catalogo.setAgendamento(Agendamento.HORARIO);
//            catalogo.setHorario(horarioRepository.findById(catalogoDTO.getIdHorario()).orElseThrow(()
//                    -> new SemResultadosException("vinculação de Horário.")));
//        } else {
//            throw new DadoInvalidoException("Escolha um tipo de agendamento e preencha somente seu campo.");
//        }
//        catalogo.setDiaSemana(catalogoDTO.getDiaSemana());
//        catalogo.setDisponibilidade(catalogoDTO.getDisponibilidade());
//
//        catalogoRepository.save(catalogo);
//    }

    @Transactional
    public void deletar(Long id) {
        Catalogo catalogo = catalogoRepository.findById(id).orElseThrow(
                () -> new SemResultadosException("exclusão"));
        catalogoRepository.delete(catalogo);
    }
}
