package com.backend.wms.service;

import com.backend.wms.dto.StockDto;
import com.backend.wms.entity.Batch;
import com.backend.wms.entity.Location;
import com.backend.wms.entity.Product;
import com.backend.wms.entity.Stock;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.StockMapper;
import com.backend.wms.repository.BatchRepository;
import com.backend.wms.repository.LocationRepository;
import com.backend.wms.repository.ProductRepository;
import com.backend.wms.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock
    private StockRepository stockRepository;
    @Mock
    private StockMapper stockMapper;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private BatchRepository batchRepository;
    @Mock
    private LocationRepository locationRepository;

    @InjectMocks
    private StockService stockService;

    private Location location;
    private Product product;
    private Batch batch;
    private Stock stock;
    private StockDto stockDto;

    @BeforeEach
    void setUp() {
        location = new Location();
        location.setId(1L);

        product = new Product();
        product.setId(2L);

        batch = new Batch();
        batch.setId(3L);

        stock = new Stock();
        stock.setId(10L);

        stockDto = StockDto.builder()
                .stockId(10L)
                .locationId(1L)
                .productId(2L)
                .batchId(3L)
                .quantity(100)
                .build();
    }

        @Test
        void createStock_Success() {
            given(locationRepository.findById(1L)).willReturn(Optional.of(location));
            given(productRepository.findById(2L)).willReturn(Optional.of(product));
            given(batchRepository.findById(3L)).willReturn(Optional.of(batch));
            given(stockMapper.toEntityWithDependencies(stockDto, location, product, batch)).willReturn(stock);
            given(stockRepository.save(stock)).willReturn(stock);
            given(stockMapper.toDto(stock)).willReturn(stockDto);

            StockDto result = stockService.createStock(stockDto);

            assertThat(result).isNotNull();
            assertThat(result.stockId()).isEqualTo(10L);

            then(stockRepository).should().save(stock);
        }

        @Test
        void createStock_LocationNotFound_ThrowsException() {
            given(locationRepository.findById(1L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> stockService.createStock(stockDto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Location not found with id: 1");

            then(productRepository).shouldHaveNoInteractions();
            then(stockRepository).shouldHaveNoInteractions();
        }

        @Test
        void createStock_ProductNotFound_ThrowsException() {
            given(locationRepository.findById(1L)).willReturn(Optional.of(location));
            given(productRepository.findById(2L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> stockService.createStock(stockDto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Product not found with id: 2");

            then(batchRepository).shouldHaveNoInteractions();
            then(stockRepository).shouldHaveNoInteractions();
        }




        @Test
        void updateStock_Success() {
            Long stockId = 10L;
            given(stockRepository.findById(stockId)).willReturn(Optional.of(stock));
            given(locationRepository.findById(1L)).willReturn(Optional.of(location));
            given(productRepository.findById(2L)).willReturn(Optional.of(product));
            given(batchRepository.findById(3L)).willReturn(Optional.of(batch));
            given(stockRepository.save(stock)).willReturn(stock);
            given(stockMapper.toDto(stock)).willReturn(stockDto);

            StockDto result = stockService.updateStock(stockId, stockDto);

            assertThat(result).isNotNull();
            then(stockMapper).should().updateEntity(stockDto, location, product, batch, stock);
            then(stockRepository).should().save(stock);
        }

        @Test
        void updateStock_StockNotFound_ThrowsException() {
            given(stockRepository.findById(99L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> stockService.updateStock(99L, stockDto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Stock not found with id: 99");

            then(locationRepository).shouldHaveNoInteractions();
            then(stockRepository).should(never()).save(any());
        }




        @Test
        void getStockById_Success() {
            given(stockRepository.findById(10L)).willReturn(Optional.of(stock));
            given(stockMapper.toDto(stock)).willReturn(stockDto);

            StockDto result = stockService.getStockById(10L);

            assertThat(result).isEqualTo(stockDto);
        }

        @Test
        void getStockById_NotFound_ThrowsException() {
            given(stockRepository.findById(10L)).willReturn(Optional.empty());

            assertThatThrownBy(() -> stockService.getStockById(10L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Stock not found with id: 10");
        }

        @Test
        void getStocksByProductId_Success() {
            Long productId = 2L;
            List<Stock> stockList = List.of(stock);
            List<StockDto> dtoList = List.of(stockDto);

            given(stockRepository.findAllByProductId(productId)).willReturn(stockList);
            given(stockMapper.toDtoList(stockList)).willReturn(dtoList);

            List<StockDto> result = stockService.getStocksByProductId(productId);

            assertThat(result).hasSize(1).containsExactly(stockDto);
        }

}