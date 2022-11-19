package com.fastcampus.sns.controller;

import com.fastcampus.sns.controller.request.PostCommentRequet;
import com.fastcampus.sns.controller.request.PostCreateRequest;
import com.fastcampus.sns.controller.request.PostModifyRequest;
import com.fastcampus.sns.controller.response.CommentResponse;
import com.fastcampus.sns.controller.response.PostResponse;
import com.fastcampus.sns.controller.response.Response;
import com.fastcampus.sns.model.Post;
import com.fastcampus.sns.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(), request.getBody(), authentication.getName());
        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modify(
            @RequestBody PostModifyRequest request, Authentication authentication,
            @PathVariable("postId") Integer postId
    ) {
        Post post = postService.modify(request.getTitle(), request.getBody(), authentication.getName(), postId);
        return Response.success(PostResponse.fromPost(post));
    }

    @DeleteMapping("/{postId}")
    public Response<Void> delete(
            Authentication authentication,
            @PathVariable("postId") Integer postId
    ) {
        postService.delete(authentication.getName(), postId);
        return Response.success();
    }

    @GetMapping
    public Response<Page<PostResponse>> list(
            Pageable pageable,
            Authentication authentication
    ) {
        return Response.success(postService.list(pageable).map(PostResponse::fromPost));
    }

    @GetMapping("/my")
    public Response<Page<PostResponse>> my(
            Pageable pageable,
            Authentication authentication
    ) {
        return Response.success(postService.my(authentication.getName(), pageable).map(PostResponse::fromPost));
    }

    @PostMapping("/{postId}/likes")
    public Response<Void> likes(
            @PathVariable("postId") Integer postId,
            Authentication authentication
    ) {
        postService.like(postId, authentication.getName());
        return Response.success();
    }

    @GetMapping("/{postId}/likes")
    public Response<Long> likeCount(
            @PathVariable("postId") Integer postId,
            Authentication authentication
    ) {
        return Response.success(
                postService.likeCount(postId)
        );
    }

    @PostMapping("/{postId}/comments")
    public Response<Void> comments(
            @PathVariable("postId") Integer postId,
            Authentication authentication,
            @RequestBody PostCommentRequet requet
    ) {
        postService.comment(postId, authentication.getName(), requet.getComment());
        return Response.success();
    }

    @GetMapping("/{postId}/comments")
    public Response<Page<CommentResponse>> comments(
            @PathVariable("postId") Integer postId,
            Authentication authentication,
            Pageable pageable
    ) {

        return Response.success(
                postService.getComments(postId, pageable).map(CommentResponse::fromComment)
        );
    }
}
