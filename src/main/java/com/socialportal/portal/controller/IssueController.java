package com.socialportal.portal.controller;

import com.socialportal.portal.dto.CommentDto;
import com.socialportal.portal.dto.IssueResponseDto;
import com.socialportal.portal.model.issues.Comment;
import com.socialportal.portal.model.issues.Issue;
import com.socialportal.portal.pojo.request.IssueRequest;
import com.socialportal.portal.service.CommentService;
import com.socialportal.portal.service.IssueService;
import com.socialportal.portal.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/main")
@RequiredArgsConstructor
public class IssueController {
    private final IssueService issueService;
    private final CommentService commentService;
    private final VoteService voteService;

    @PostMapping("/issues")
    ResponseEntity<Issue> addIssue(@RequestPart IssueRequest issue,
                                   @RequestPart(required = false) List<MultipartFile> images,
                                   Authentication authentication) {
        return new ResponseEntity<>(this.issueService.save(issue, images, authentication), HttpStatus.CREATED);
    }

    @GetMapping("/issues")
    ResponseEntity<Page<IssueResponseDto>> listIssues
            (
                    Authentication authentication,
                    @RequestParam Integer pageNo,
                    @RequestParam Integer noOfItems
            ) {
        return ResponseEntity.ok(this.issueService.getIssues(authentication, pageNo, noOfItems));
    }

    @GetMapping("/issues/{issueId}/comments")
    ResponseEntity<Page<CommentDto>> getCommentsByIssueId
            (
                    @PathVariable(name = "issueId") Long issueId,
                    @RequestParam(name = "pageNo") int pageNo,
                    @RequestParam(name = "itemsPerPage") int itemsPerPage
            ) {
        return ResponseEntity.ok(this.commentService.getComments(issueId, pageNo, itemsPerPage));
    }

    @PostMapping("/issues/{issueId}/comments")
    ResponseEntity<CommentDto> addComment
            (
                    Authentication authentication,
                    @RequestBody Comment comment,
                    @PathVariable long issueId
            ) {
        return new ResponseEntity<>(this.commentService.addComment(authentication, comment, issueId), HttpStatus.CREATED);
    }

    @PostMapping("/issues/{issueId}/vote")
    ResponseEntity<Integer> vote(@PathVariable Long issueId, Authentication authentication, @RequestParam Integer voteValue) {
        return ResponseEntity.ok(this.voteService.vote(authentication, voteValue, issueId));
    }
    @DeleteMapping("/comments/{commentId}")
    ResponseEntity<String> deleteComment(Authentication authentication, @PathVariable(name = "commentId") Long commentId) {
        this.commentService.deleteComment(authentication, commentId);
        return new ResponseEntity<>("Comment deleted successfully", HttpStatus.OK);
    }

}
