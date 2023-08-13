package com.socialportal.portal.service.impl;


import com.socialportal.portal.dto.IssueResponseDto;
import com.socialportal.portal.model.geo.IssueLocation;
import com.socialportal.portal.pojo.request.IssueRequest;
import com.socialportal.portal.model.issues.Issue;
import com.socialportal.portal.repository.IssueLocationRepository;
import com.socialportal.portal.repository.IssueRepository;
import com.socialportal.portal.repository.UserEntityRepository;
import com.socialportal.portal.service.IssueService;
import com.socialportal.portal.service.VoteService;
import com.socialportal.portal.service.utils.Slicer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {
    private final IssueRepository issueRepository;
    private final IssueLocationRepository issueLocationRepository;
    private final UserEntityRepository userEntityRepository;
    private final VoteService voteService;

    public Issue save(IssueRequest issueRequest) {
        Issue issue = issueRequest.getIssue();
        issue.setIssueLocation(issueRequest.getIssueLocation());

        return this.issueRepository.save(issue);
    }

    @Override
    public Page<IssueResponseDto> getIssues(Authentication authentication, int pageNumber, int pageSize) {

        List<Issue> issues = this.findIssuesByUserInterestZone(authentication.getName());

        List<IssueResponseDto> content = mapIssuesToDto(issues);

        List<IssueResponseDto> paginatedContent = Slicer.sliceContent(content, pageNumber, pageSize);

        return new PageImpl<>(paginatedContent, PageRequest.of(pageNumber, pageSize), paginatedContent.size());
    }

    private List<Issue> findIssuesByUserInterestZone(String username) {
        var coordinates = getUserInterestZone(username);
        List<IssueLocation> issueLocations = this.issueLocationRepository.findAllByLatitudeBetweenAndLongitudeBetween
                (
                        coordinates.get(0),
                        coordinates.get(1),
                        coordinates.get(2),
                        coordinates.get(3)
                );

        return issueLocations.stream().map(IssueLocation::getIssue).toList();
    }

    private List<Double> getUserInterestZone(String username) {
        var user = this.userEntityRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User cannot be located in db. Database corrupted!!"));
        var userLocation = user.getUserLocation();

        return getBoundingBox
                (
                        userLocation.getLatitude(),
                        userLocation.getLongitude(),
                        userLocation.getRadiusOfInterest() * 1000.0 // from km to meters
                );
    }

    private List<IssueResponseDto> mapIssuesToDto(List<Issue> issueLocations) {
        return issueLocations
                .stream()
                .map(issue ->
                        {
                            var voteDto = this.voteService.getVotesByIssueId(issue.getId());
                            return new IssueResponseDto
                                    (
                                            issue.getId(),
                                            issue.getTitle(),
                                            issue.getDescription(),
                                            voteDto.getUpVotes(),
                                            voteDto.getDownVotes()
                                    );
                        }
                )
                .toList();
    }

    private List<Double> getBoundingBox(final double pLatitude, final double pLongitude, double pDistanceInMeters) {
        final List<Double> boundingBox = new ArrayList<>();

        final double latRadian = Math.toRadians(pLatitude);

        final double degLatKm = 110.574235;
        final double degLongKm = 110.572833 * Math.cos(latRadian);
        final double deltaLat = pDistanceInMeters / 1000.0 / degLatKm;
        final double deltaLong = pDistanceInMeters / 1000.0 / degLongKm;

        final double minLat = pLatitude - deltaLat;
        final double minLong = pLongitude - deltaLong;
        final double maxLat = pLatitude + deltaLat;
        final double maxLong = pLongitude + deltaLong;

        boundingBox.add(minLat);
        boundingBox.add(maxLat);
        boundingBox.add(minLong);
        boundingBox.add(maxLong);

        return boundingBox;
    }

}
