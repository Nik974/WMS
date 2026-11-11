package com.backend.wms.service;

import com.backend.wms.dto.AdjustStockRequest;
import com.backend.wms.dto.StockDto;
import com.backend.wms.entity.*;
import com.backend.wms.exception.ResourceNotFoundException;
import com.backend.wms.mapper.StockMapper;
import com.backend.wms.repository.*;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

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
    @Mock
    private UserRepository userRepository;
    @Mock
    private StockMovementRepository movementRepository;

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
    void getStocks() {
        Long productId = 1L;
        Long warehouseId = 2L;
        Stock stock = new Stock();
        StockDto dto = StockDto.builder().build();

        when(stockRepository.search(productId, warehouseId)).thenReturn(List.of(stock));
        when(stockMapper.toDtoList(List.of(stock))).thenReturn(List.of(dto));

        List<StockDto> result = stockService.getStocks(productId, warehouseId);

        assertThat(result).hasSize(1).contains(dto);
        verify(stockRepository).search(productId, warehouseId);
    }

    @Test
    void getStockById_NotFound() {
        Long id = 1L;
        when(stockRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> stockService.getStockById(id))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void receive_NewStock() {
        Location location = new Location();
        location.setId(1L);
        Product product = new Product();
        product.setId(2L);
        Batch batch = new Batch();
        batch.setId(3L);

        Stock newStock = new Stock();
        newStock.setLocation(location);
        newStock.setProduct(product);
        newStock.setBatch(batch);
        newStock.setQuantity(0);

        when(stockRepository.findForUpdate(1L, 2L, 3L)).thenReturn(Optional.empty());
        when(stockRepository.saveAndFlush(any(Stock.class))).thenReturn(newStock);
        when(userRepository.getReferenceById(10L)).thenReturn(new User());

        stockService.receive(location, product, batch, 50, 99L, 10L);

        assertThat(newStock.getQuantity()).isEqualTo(50);
        verify(stockRepository).saveAndFlush(any(Stock.class));
        verify(movementRepository).save(any(StockMovement.class));
    }

    @Test
    void receive_ExistingStock() {
        Location location = new Location();
        location.setId(1L);
        Product product = new Product();
        product.setId(2L);
        Batch batch = new Batch();
        batch.setId(3L);

        Stock existingStock = new Stock();
        existingStock.setLocation(location);
        existingStock.setProduct(product);
        existingStock.setBatch(batch);
        existingStock.setQuantity(20);

        when(stockRepository.findForUpdate(1L, 2L, 3L)).thenReturn(Optional.of(existingStock));
        when(userRepository.getReferenceById(10L)).thenReturn(new User());

        stockService.receive(location, product, batch, 30, 99L, 10L);

        assertThat(existingStock.getQuantity()).isEqualTo(50);
        verify(stockRepository, never()).saveAndFlush(any());
        verify(movementRepository).save(any(StockMovement.class));
    }

    @Test
    void ship_Success() {
        Product product = new Product();
        product.setId(1L);

        OutboundItem item = new OutboundItem();
        item.setProduct(product);
        item.setQuantity(15);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(2L);

        OutboundOrder order = new OutboundOrder();
        order.setId(100L);
        order.setWarehouse(warehouse);
        order.setItems(List.of(item));

        Stock stock1 = new Stock();
        stock1.setProduct(product);
        stock1.setLocation(new Location());
        stock1.setQuantity(10);
        stock1.setReservedQuantity(0);

        Stock stock2 = new Stock();
        stock2.setProduct(product);
        stock2.setLocation(new Location());
        stock2.setQuantity(20);
        stock2.setReservedQuantity(5);

        when(stockRepository.findAvailableForShipment(eq(1L), eq(2L), any(LocalDate.class)))
                .thenReturn(List.of(stock1, stock2));
        when(userRepository.getReferenceById(10L)).thenReturn(new User());

        stockService.ship(order, 10L);

        assertThat(stock1.getQuantity()).isZero();
        assertThat(stock2.getQuantity()).isEqualTo(15);
        verify(movementRepository, times(2)).save(any(StockMovement.class));
    }

    @Test
    void ship_NotEnoughStock() {
        Product product = new Product();
        product.setId(1L);
        product.setProductName("TestProduct");

        OutboundItem item = new OutboundItem();
        item.setProduct(product);
        item.setQuantity(15);

        Warehouse warehouse = new Warehouse();
        warehouse.setId(2L);

        OutboundOrder order = new OutboundOrder();
        order.setWarehouse(warehouse);
        order.setItems(List.of(item));

        when(stockRepository.findAvailableForShipment(eq(1L), eq(2L), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> stockService.ship(order, 10L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void adjust_Success() {
        AdjustStockRequest req = new AdjustStockRequest(1L, 2L, 3L, 50, "Audit");

        Location location = new Location();
        location.setId(1L);

        Product product = new Product();
        product.setId(2L);

        Batch batch = new Batch();
        batch.setId(3L);
        batch.setProduct(product);

        Stock stock = new Stock();
        stock.setLocation(location);
        stock.setProduct(product);
        stock.setBatch(batch);
        stock.setQuantity(30);
        stock.setReservedQuantity(0);

        StockDto expectedDto = StockDto.builder().build();

        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(batchRepository.findById(3L)).thenReturn(Optional.of(batch));
        when(stockRepository.findForUpdate(1L, 2L, 3L)).thenReturn(Optional.of(stock));
        when(userRepository.getReferenceById(10L)).thenReturn(new User());
        when(stockMapper.toDto(stock)).thenReturn(expectedDto);

        StockDto result = stockService.adjust(req, 10L);

        assertThat(result).isEqualTo(expectedDto);
        assertThat(stock.getQuantity()).isEqualTo(50);
        verify(movementRepository).save(any(StockMovement.class));
    }

    @Test
    void adjust_BatchDoesNotBelongToProduct() {
        AdjustStockRequest req = new AdjustStockRequest(1L, 2L, 3L, 50, "Audit");

        Location location = new Location();

        Product product = new Product();
        product.setId(2L);

        Product otherProduct = new Product();
        otherProduct.setId(99L);

        Batch batch = new Batch();
        batch.setId(3L);
        batch.setProduct(otherProduct);

        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(batchRepository.findById(3L)).thenReturn(Optional.of(batch));

        assertThatThrownBy(() -> stockService.adjust(req, 10L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void adjust_QuantityLowerThanReserved() {
        AdjustStockRequest req = new AdjustStockRequest(1L, 2L, 3L, 5, "Audit");

        Location location = new Location();
        location.setId(1L);

        Product product = new Product();
        product.setId(2L);

        Batch batch = new Batch();
        batch.setId(3L);
        batch.setProduct(product);

        Stock stock = new Stock();
        stock.setQuantity(30);
        stock.setReservedQuantity(10);

        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(batchRepository.findById(3L)).thenReturn(Optional.of(batch));
        when(stockRepository.findForUpdate(1L, 2L, 3L)).thenReturn(Optional.of(stock));

        assertThatThrownBy(() -> stockService.adjust(req, 10L))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void adjust_QuantityUnchanged() {
        AdjustStockRequest req = new AdjustStockRequest(1L, 2L, 3L, 30, "Audit");

        Location location = new Location();
        location.setId(1L);

        Product product = new Product();
        product.setId(2L);

        Batch batch = new Batch();
        batch.setId(3L);
        batch.setProduct(product);

        Stock stock = new Stock();
        stock.setQuantity(30);
        stock.setReservedQuantity(0);

        when(locationRepository.findById(1L)).thenReturn(Optional.of(location));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(batchRepository.findById(3L)).thenReturn(Optional.of(batch));
        when(stockRepository.findForUpdate(1L, 2L, 3L)).thenReturn(Optional.of(stock));

        assertThatThrownBy(() -> stockService.adjust(req, 10L))
                .isInstanceOf(IllegalArgumentException.class);
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

            when(stockRepository.search(productId, null)).thenReturn(stockList);
            when(stockMapper.toDtoList(stockList)).thenReturn(dtoList);

            List<StockDto> result = stockService.getStocks(productId, null);

            assertThat(result).hasSize(1).containsExactly(stockDto);
        }

}