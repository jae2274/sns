package com.fastcampus.sns.repository;

import com.fastcampus.sns.model.entity.LikeEntity;
import com.fastcampus.sns.model.entity.PostEntity;
import com.fastcampus.sns.model.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface LikeEntityRepository extends JpaRepository<LikeEntity, Integer> {
    Optional<LikeEntity> findByUserAndPost(UserEntity userEntity, PostEntity postEntity);

    long countByPost(PostEntity postEntity);

    @Transactional
    @Modifying
    @Query("UPDATE LikeEntity entity SET entity.deletedAt = current_timestamp  WHERE entity.post = :postEntity")
    int deleteAllByPost(@Param("postEntity") PostEntity postEntity);
}
