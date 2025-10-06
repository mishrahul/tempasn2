package in.taxgenie.repositories;

import in.taxgenie.entities.OemMaster;
import in.taxgenie.entities.OnboardingProcess;
import in.taxgenie.entities.Vendor;
import in.taxgenie.entities.enums.OnboardingStatus;
import in.taxgenie.repositories.base.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OnboardingProcess entity
 * 
 * @author ASN Development Team
 * @version 1.0.0
 */
@Repository
public interface OnboardingProcessRepository extends BaseRepository<OnboardingProcess> {

    /**
     * Find onboarding process by vendor and OEM
     * 
     * @param vendor the vendor
     * @param oem the OEM
     * @return Optional containing the onboarding process if found
     */
    Optional<OnboardingProcess> findByVendorAndOem(Vendor vendor, OemMaster oem);

    /**
     * Find onboarding processes by vendor
     * 
     * @param vendor the vendor
     * @return List of onboarding processes
     */
    List<OnboardingProcess> findByVendor(Vendor vendor);

    /**
     * Find onboarding processes by OEM
     * 
     * @param oem the OEM
     * @return List of onboarding processes
     */
    List<OnboardingProcess> findByOem(OemMaster oem);

    /**
     * Find onboarding processes by status
     * 
     * @param status the onboarding status
     * @return List of onboarding processes
     */
    List<OnboardingProcess> findByStatus(OnboardingStatus status);

    /**
     * Find onboarding processes by current step
     * 
     * @param currentStep the current step
     * @return List of onboarding processes
     */
    List<OnboardingProcess> findByCurrentStep(String currentStep);

    /**
     * Find onboarding processes by OEM and status
     * 
     * @param oem the OEM
     * @param status the onboarding status
     * @return List of onboarding processes
     */
    List<OnboardingProcess> findByOemAndStatus(OemMaster oem, OnboardingStatus status);

    /**
     * Find onboarding processes by vendor and status
     * 
     * @param vendor the vendor
     * @param status the onboarding status
     * @return List of onboarding processes
     */
    List<OnboardingProcess> findByVendorAndStatus(Vendor vendor, OnboardingStatus status);

    /**
     * Find onboarding processes started after given date
     * 
     * @param dateTime the date time threshold
     * @return List of onboarding processes
     */
    List<OnboardingProcess> findByStartedAtAfter(LocalDateTime dateTime);

    /**
     * Find completed onboarding processes
     * 
     * @return List of completed onboarding processes
     */
    List<OnboardingProcess> findByStatusAndCompletedAtIsNotNull(OnboardingStatus status);

    /**
     * Find stale onboarding processes (in progress but not updated recently)
     * 
     * @param dateTime the date time threshold
     * @return List of stale onboarding processes
     */
    @Query("SELECT op FROM OnboardingProcess op WHERE op.status = 'IN_PROGRESS' AND op.lastUpdatedAt < :dateTime")
    List<OnboardingProcess> findStaleOnboardingProcesses(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Find onboarding processes by progress percentage range
     * 
     * @param minProgress the minimum progress percentage
     * @param maxProgress the maximum progress percentage
     * @return List of onboarding processes
     */
    @Query("SELECT op FROM OnboardingProcess op WHERE op.progressPercentage BETWEEN :minProgress AND :maxProgress")
    List<OnboardingProcess> findByProgressPercentageBetween(@Param("minProgress") Integer minProgress, 
                                                           @Param("maxProgress") Integer maxProgress);

    /**
     * Count onboarding processes by OEM and status
     * 
     * @param oem the OEM
     * @param status the onboarding status
     * @return count of onboarding processes
     */
    long countByOemAndStatus(OemMaster oem, OnboardingStatus status);

    /**
     * Count onboarding processes by status
     * 
     * @param status the onboarding status
     * @return count of onboarding processes
     */
    long countByStatus(OnboardingStatus status);

    /**
     * Find recently started onboarding processes
     * 
     * @param dateTime the date time threshold
     * @return List of recently started onboarding processes
     */
    @Query("SELECT op FROM OnboardingProcess op WHERE op.startedAt >= :dateTime ORDER BY op.startedAt DESC")
    List<OnboardingProcess> findRecentlyStarted(@Param("dateTime") LocalDateTime dateTime);

    /**
     * Find onboarding processes by deployment method
     * 
     * @param deploymentMethod the deployment method
     * @return List of onboarding processes
     */
    List<OnboardingProcess> findByDeploymentMethod(String deploymentMethod);

    /**
     * Check if onboarding process exists for vendor and OEM
     *
     * @param vendor the vendor
     * @param oem the OEM
     * @return true if exists, false otherwise
     */
    boolean existsByVendorAndOem(Vendor vendor, OemMaster oem);

    /**
     * Find onboarding process by vendor, OEM and company code
     *
     * @param vendor the vendor
     * @param oem the OEM
     * @param companyCode the company code
     * @return Optional containing the onboarding process if found
     */
    @Query("SELECT op FROM OnboardingProcess op WHERE op.vendor = :vendor AND op.oem = :oem AND op.companyCode = :companyCode")
    Optional<OnboardingProcess> findByVendorAndOemAndCompanyCode(@Param("vendor") Vendor vendor,
                                                                @Param("oem") OemMaster oem,
                                                                @Param("companyCode") Long companyCode);
}
