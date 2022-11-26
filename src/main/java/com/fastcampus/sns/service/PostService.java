package com.fastcampus.sns.service;

import com.fastcampus.sns.exception.ErrorCode;
import com.fastcampus.sns.exception.SnsApplicationException;
import com.fastcampus.sns.model.AlarmArgs;
import com.fastcampus.sns.model.AlarmType;
import com.fastcampus.sns.model.Comment;
import com.fastcampus.sns.model.Post;
import com.fastcampus.sns.model.entity.CommentEntity;
import com.fastcampus.sns.model.entity.LikeEntity;
import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import com.fastcampus.sns.model.event.AlarmEvent;
import com.fastcampus.sns.producer.AlarmProducer;
import com.fastcampus.sns.repository.CommentEntityRepository;
import com.fastcampus.sns.repository.LikeEntityRepository;
import com.fastcampus.sns.repository.PostEntityRepository;
import com.fastcampus.sns.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserRepository userRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmProducer alarmProducer;


    @Transactional
    public void create(String title, String body, String userName) {
        UserEntity userEntity = getUserOrException(userName);

        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }

    @Transactional
    public Post modify(String title, String body, String userName, Integer postId) {
        UserEntity userEntity = getUserOrException(userName);
        PostEntity postEntity = getPostOrException(postId);

        if (postEntity.getUser() != userEntity)
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
//        posetEntityRepository.findById(
    }

    @Transactional
    public void delete(String userName, Integer postId) {
        UserEntity userEntity = getUserOrException(userName);
        PostEntity postEntity = getPostOrException(postId);

        if (postEntity.getUser() != userEntity)
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));

        likeEntityRepository.deleteAllByPost(postEntity);
        commentEntityRepository.deleteAllByPost(postEntity);
        postEntityRepository.delete(postEntity);
    }

    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable)
                .map(postEntity -> Post.fromEntity(postEntity));
    }

    public Page<Post> my(String userName, Pageable pageable) {
        UserEntity userEntity = getUserOrException(userName);

        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }

    @Transactional
    public void like(Integer postId, String userName) {
        UserEntity userEntity = getUserOrException(userName);
        PostEntity postEntity = getPostOrException(postId);

        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(
                it -> {
                    throw new SnsApplicationException(ErrorCode.ALREADY_LIKE, String.format("userName %s already like post %d", userName, postId));
                }
        );

        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));
        alarmProducer.send(new AlarmEvent(postEntity.getUser().getId(), AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(userEntity.getId(), postEntity.getId())));
        ;
    }

    public long likeCount(Integer postId) {
        PostEntity postEntity = getPostOrException(postId);

        return likeEntityRepository.countByPost(postEntity);
    }

    @Transactional
    public void comment(Integer postId, String userName, String comment) {
        UserEntity userEntity = getUserOrException(userName);
        PostEntity postEntity = getPostOrException(postId);

        commentEntityRepository.save(CommentEntity.of(userEntity, postEntity, comment));
        alarmProducer.send(new AlarmEvent(postEntity.getUser().getId(), AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(userEntity.getId(), postEntity.getId())));
    }

    public Page<Comment> getComments(Integer postId, Pageable pageable) {
        PostEntity postEntity = getPostOrException(postId);
        return commentEntityRepository.findAllByPost(postEntity, pageable).map(Comment::fromEntity);
    }

    private PostEntity getPostOrException(Integer postId) {
        return postEntityRepository.findById(postId).orElseThrow(() -> new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not found", postId)));
    }

    private UserEntity getUserOrException(String userName) {
        return userRepository.findByUsername(userName).orElseThrow(() -> new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
    }
}
