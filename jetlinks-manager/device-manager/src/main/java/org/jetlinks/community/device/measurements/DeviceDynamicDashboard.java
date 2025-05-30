package org.jetlinks.community.device.measurements;

import org.hswebframework.ezorm.rdb.mapping.ReactiveRepository;
import org.jetlinks.core.device.DeviceRegistry;
import org.jetlinks.core.event.EventBus;
import org.jetlinks.community.dashboard.DashboardObject;
import org.jetlinks.community.device.entity.DeviceProductEntity;
import org.jetlinks.community.device.service.data.DeviceDataService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@Component
public class DeviceDynamicDashboard implements DeviceDashboard {

    private final ReactiveRepository<DeviceProductEntity,String> productRepository;

    private final DeviceRegistry registry;

    private final EventBus eventBus;

    private final DeviceDataService dataService;

    public DeviceDynamicDashboard(ReactiveRepository<DeviceProductEntity,String> productRepository,
                                  DeviceRegistry registry,
                                  DeviceDataService deviceDataService,
                                  EventBus eventBus) {
        this.productRepository = productRepository;
        this.registry = registry;
        this.eventBus = eventBus;
        this.dataService = deviceDataService;
    }

    @PostConstruct
    public void init() {
        //设备状态变更
    }

    @Override
    public Flux<DashboardObject> getObjects() {
        return productRepository
            .createQuery()
            .fetch()
            .flatMap(this::convertObject);
    }

    @Override
    public Mono<DashboardObject> getObject(String id) {
        return productRepository
            .findById(id)
            .flatMap(this::convertObject);
    }

    protected Mono<DeviceDashboardObject> convertObject(DeviceProductEntity product) {
        return registry
            .getProduct(product.getId())
            .map(operator -> DeviceDashboardObject.of(product.getId(), product.getName(), operator, eventBus, dataService,registry));
    }
}
