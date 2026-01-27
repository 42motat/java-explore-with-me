package ru.practicum.explorewithme.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.explorewithme.model.Location;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationMapper {
    public static Location mapToLocation(double lat, double lon) {
        Location location = new Location();
        location.setLat(lat);
        location.setLon(lon);
        return location;
    }
}
