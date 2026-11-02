import { ComponentFixture, TestBed } from '@angular/core/testing';
import { InboundOrders } from './inbound-orders';

describe('InboundOrders', () => {
  let component: InboundOrders;
  let fixture: ComponentFixture<InboundOrders>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InboundOrders],
    }).compileComponents();

    fixture = TestBed.createComponent(InboundOrders);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
