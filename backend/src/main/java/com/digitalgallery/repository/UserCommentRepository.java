package com.digitalgallery.repository;

import com.digitalgallery.entity.User;
import com.digitalgallery.entity.UserComment;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCommentRepository extends JpaRepository<UserComment, UUID> {
  List<UserComment> findByTargetUserOrderByCreatedAtAsc(User targetUser);
}
