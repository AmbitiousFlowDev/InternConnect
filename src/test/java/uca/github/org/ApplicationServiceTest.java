package uca.github.org;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uca.github.org.models.Application;
import uca.github.org.models.User;
import uca.github.org.repositories.ApplicationRepository;
import uca.github.org.services.ApplicationServiceImpl;
import java.util.* ;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock
    private ApplicationRepository applicationRepository;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    @Test
    void getUserApplications_returnsListForUser() {
        User user = new User();
        when(applicationRepository.findByApplicantOrderBySubmittedAtDesc(user,Application.ApplicationStatus.SUBMITTED))
                .thenReturn(List.of());

        List<Application> result = applicationService.getUserApplications(user);

        assertNotNull(result);
        verify(applicationRepository).findByApplicantOrderBySubmittedAtDesc(user,Application.ApplicationStatus.SUBMITTED);
    }

    @Test
    void getUserApplicationsByStatus_returnsFilteredList() {
        User user = new User();
        when(applicationRepository.findByApplicantAndStatusOrderBySubmittedAtDesc(
                user, Application.ApplicationStatus.SUBMITTED))
                .thenReturn(List.of());

        List<Application> result = applicationService.getUserApplicationsByStatus(
                user, Application.ApplicationStatus.SUBMITTED);

        assertNotNull(result);
        verify(applicationRepository)
                .findByApplicantAndStatusOrderBySubmittedAtDesc(
                        user, Application.ApplicationStatus.SUBMITTED);
    }
}