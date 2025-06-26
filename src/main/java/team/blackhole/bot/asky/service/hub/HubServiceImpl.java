package team.blackhole.bot.asky.service.hub;

import com.google.common.eventbus.EventBus;
import com.google.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import team.blackhole.bot.asky.db.hibernate.HibernateRepository;
import team.blackhole.bot.asky.db.hibernate.domains.Hub;
import team.blackhole.bot.asky.db.hibernate.repository.HubRepository;
import team.blackhole.bot.asky.events.HubCreatedEvent;
import team.blackhole.bot.asky.service.hub.data.CreateHubData;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для работы с хабами {@link HubService}
 */
@RequiredArgsConstructor(onConstructor_ = @__(@Inject))
public class HubServiceImpl implements HubService {

    /** Репозиторий для работы с хабами */
    private final HubRepository hubRepository;

    /** Шина событий */
    private final EventBus eventBus;

    @Override
    @Transactional
    public Hub create(CreateHubData data) {
        var hub = new Hub();

        hub.setChannelHubId(data.channelHubId());
        hub.setChannelId(data.channelId());
        hub.setName(data.name());
        hub.setType(data.type());
        hub = hubRepository.save(hub);

        eventBus.post(new HubCreatedEvent(hub));

        return hub;
    }

    @Override
    public Optional<Hub> findHubByChannelHubIdAndChannelId(String channelId, String channelHubId) {
        return hubRepository.findHubByChannelHubIdAndChannelId(channelId, channelHubId);
    }

    @Override
    public List<Hub> findHubsByChannelId(Collection<String> channelIds) {
        return hubRepository.findHubsByChannelId(channelIds);
    }

    @Override
    public HibernateRepository<Hub, Long> getRepository() {
        return hubRepository;
    }
}
