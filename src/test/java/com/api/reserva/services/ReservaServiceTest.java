package com.api.reserva.services;

import com.api.reserva.dto.ReservaDTO;
import com.api.reserva.dto.ReservaReferenciaDTO;
import com.api.reserva.entity.Catalogo;
import com.api.reserva.entity.Reserva;
import com.api.reserva.entity.Usuario;
import com.api.reserva.enums.StatusReserva;
import com.api.reserva.exception.DataInvalidaException;
import com.api.reserva.exception.SemResultadosException;
import com.api.reserva.repository.CatalogoRepository;
import com.api.reserva.repository.ReservaRepository;
import com.api.reserva.repository.UsuarioRepository;
import com.api.reserva.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CatalogoRepository catalogoRepository;

    @InjectMocks
    private ReservaService reservaService;

    private ReservaDTO reservaDTO;
    private Usuario usuario;
    private Catalogo catalogo;

    @BeforeEach
    void setup() {
        usuario = new Usuario();
        usuario.setNome("João");

        catalogo = new Catalogo();
        catalogo.setDiaSemana(catalogoDTO.getDiaSemana()); // mesmo dia para não gerar exceção

        reservaDTO = new ReservaDTO();
        reservaDTO.setHostId(1L);
        reservaDTO.setCatalogoId(1L);
        reservaDTO.setData(LocalDate.now());
        reservaDTO.setHoraInicio(LocalTime.of(10, 0));
        reservaDTO.setHoraFim(LocalTime.of(12, 0));
        reservaDTO.setMsgInterna("Teste interno");
    }

    // ✅ Caso de sucesso - deve salvar reserva corretamente
    @Test
    void deveSalvarReservaComSucesso() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(catalogoRepository.findById(1L)).thenReturn(Optional.of(catalogo));

        assertDoesNotThrow(() -> reservaService.salvar(reservaDTO));

        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    // ⚠️ Deve lançar exceção se usuário não for encontrado
    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SemResultadosException.class, () -> reservaService.salvar(reservaDTO));

        verify(reservaRepository, never()).save(any());
    }

    // ⚠️ Deve lançar exceção se catálogo não for encontrado
    @Test
    void deveLancarExcecaoQuandoCatalogoNaoEncontrado() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(catalogoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(SemResultadosException.class, () -> reservaService.salvar(reservaDTO));

        verify(reservaRepository, never()).save(any());
    }

    // ⚠️ Deve lançar exceção se data não coincidir com o dia do catálogo
    @Test
    void deveLancarExcecaoQuandoDataInvalida() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        catalogo.setDiaSemana(reservaDTO.getData().plusDays(1)); // dia diferente
        when(catalogoRepository.findById(1L)).thenReturn(Optional.of(catalogo));

        assertThrows(DataInvalidaException.class, () -> reservaService.salvar(reservaDTO));

        verify(reservaRepository, never()).save(any());
    }

    // ✅ Deve buscar todas as reservas
    @Test
    void deveRetornarListaDeReservas() {
        when(reservaRepository.findAll()).thenReturn(List.of(new Reserva()));

        List<ReservaReferenciaDTO> resultado = reservaService.buscar();

        assertEquals(1, resultado.size());
        verify(reservaRepository, times(1)).findAll();
    }

    // ✅ Deve deletar uma reserva
    @Test
    void deveDeletarReserva() {
        Reserva reserva = new Reserva();
        reserva.setId(1L);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        reservaService.deletar(1L);

        verify(reservaRepository, times(1)).delete(reserva);
    }
}
