package hu.tilos.radio.backend.event;

import hu.tilos.radio.backend.data.response.CreateResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    EventRepository eventRepository;

    public CreateResponse insert(String episodeId, Event event) {
        event.setEpisodeId(episodeId);
        eventRepository.insert(event);
        if (event.getType() == EventType.TRACK) {
            //TODO notify IRC
        }
        return new CreateResponse(true);
    }

    public List<Event> findEvents(String id) {
        return eventRepository.findByEpisodeId(id);
    }
}
