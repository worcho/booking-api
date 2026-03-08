package io.github.dvirisha.booking_api.dev;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dev")
@Profile("dev")
@RequiredArgsConstructor
public class DevSeedController {

    private final SeedService seedService;

    @PostMapping("/seed")
    public ResponseEntity<String> seed(
            @RequestParam(defaultValue = "100") int rooms,
            @RequestParam(defaultValue = "10") int bookingsPerRoom
    ) {
        seedService.seed(rooms, bookingsPerRoom);
        return ResponseEntity.ok(
                "Seed completed. rooms=" + rooms + ", bookingsPerRoom=" + bookingsPerRoom
        );
    }
}
