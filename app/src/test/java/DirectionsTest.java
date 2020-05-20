import com.ingsoft.bancoapp.applicationForm.ApplicantDetailsActivity;

import org.junit.Test;
import static com.google.common.truth.Truth.assertThat;

public class DirectionsTest {
    private static final String direction = "Avenida Ing. Luis P. Ponce 1375";
    @Test
    public void directionValidator_ReturnsTrue() {
        assertThat(ApplicantDetailsActivity.getDirection1()).isEqualTo(direction);

        assertThat(ApplicantDetailsActivity.getDirection2()).isEqualTo(direction);
    }
}
