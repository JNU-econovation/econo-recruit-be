package com.econovation.recruit.api.comment.service;

import com.econovation.recruit.api.comment.usecase.CommentUseCase;
import com.econovation.recruit.api.config.security.SecurityUtils;
import com.econovation.recruitcommon.annotation.InvalidateCacheByCreateComment;
import com.econovation.recruitcommon.annotation.InvalidateCacheByCommentId;
import com.econovation.recruitcommon.annotation.InvalidateCacheByDeleteComment;
import com.econovation.recruitcommon.utils.Result;
import com.econovation.recruitdomain.common.aop.redissonLock.RedissonLock;
import com.econovation.recruitdomain.domains.card.domain.Card;
import com.econovation.recruitdomain.domains.comment.domain.Comment;
import com.econovation.recruitdomain.domains.comment.domain.CommentLike;
import com.econovation.recruitdomain.domains.comment.exception.CommentInvalidCreatedException;
import com.econovation.recruitdomain.domains.comment.exception.CommentNotHostException;
import com.econovation.recruitdomain.domains.dto.CommentPairVo;
import com.econovation.recruitdomain.domains.dto.CommentRegisterDto;
import com.econovation.recruitdomain.domains.interviewer.domain.Interviewer;
import com.econovation.recruitdomain.out.CardLoadPort;
import com.econovation.recruitdomain.out.CommentLikeLoadPort;
import com.econovation.recruitdomain.out.CommentLikeRecordPort;
import com.econovation.recruitdomain.out.CommentLoadPort;
import com.econovation.recruitdomain.out.CommentRecordPort;
import com.econovation.recruitdomain.out.InterviewerLoadPort;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService implements CommentUseCase {
    private final CommentRecordPort commentRecordPort;
    private final CommentLoadPort commentLoadPort;
    private final CommentLikeRecordPort commentLikeRecordPort;
    private final CommentLikeLoadPort commentLikeLoadPort;
    private final CardLoadPort cardLoadPort;
    private final InterviewerLoadPort interviewerLoadPort;

    @Override
    @Transactional
    @InvalidateCacheByCreateComment
    public Comment saveComment(CommentRegisterDto commentDto) {
        Long userId = SecurityUtils.getCurrentUserId();
        // applicantId null 이면 "" 으로 바꿔준다. cardId 가 null 이면 0 으로 바꿔준다.
        Comment comment = convertComment(commentDto, userId);
        Comment loadedComment = commentRecordPort.saveComment(comment);
        // 지원서 카드면 카드 타입이지만
        if (comment.isApplicantComment()) {
            Card card = cardLoadPort.findByApplicantId(comment.getApplicantId());
            card.plusCommentCount();
            return loadedComment;
        }
        // 카드 타입이면서 지원서 타입이면 안된다.
        else {
            Card card = cardLoadPort.findById(comment.getCardId());
            card.plusCommentCount();
            return loadedComment;
        }
    }

    private Comment convertComment(CommentRegisterDto commentDto, Long userId) {
        if (commentDto.getApplicantId() == null) {
            return Comment.builder()
                    .content(commentDto.getContent())
                    .applicantId("")
                    .cardId(commentDto.getCardId())
                    .parentId(commentDto.getParentCommentId())
                    .idpId(userId)
                    .build();
        } else if (commentDto.getCardId() == null) {
            return Comment.builder()
                    .content(commentDto.getContent())
                    .applicantId(commentDto.getApplicantId())
                    .cardId(0L)
                    .parentId(commentDto.getParentCommentId())
                    .idpId(userId)
                    .build();
        }
        throw CommentInvalidCreatedException.EXCEPTION;
    }

    @Override
    @Transactional
    @InvalidateCacheByCommentId
    public void deleteComment(Long commentId) {
        Long idpId = SecurityUtils.getCurrentUserId();
        Comment comment = commentLoadPort.findById(commentId);
        // 댓글 작성자만 삭제가 가능하다.
        validateHost(comment, idpId);

        decreaseCommentCount(comment);

        commentRecordPort.deleteComment(comment);

        List<CommentLike> commentLikes = commentLikeLoadPort.getByCommentId(commentId);
        commentLikeRecordPort.deleteAll(commentLikes);
    }

    private void validateHost(Comment comment, Long idpId) {
        if (!comment.isHost(idpId)) {
            throw CommentNotHostException.EXCEPTION;
        }
    }

    private void decreaseCommentCount(Comment comment) {
        if (comment.getCardId() == 0 || comment.getCardId() == null) {
            cardLoadPort.findByApplicantId(comment.getApplicantId()).minusCommentCount();
        } else {
            cardLoadPort.findById(comment.getCardId()).minusCommentCount();
        }
    }

    @Override
    public Comment findById(Long commentId) {
        return commentLoadPort.findById(commentId);
    }

    @Override
    @RedissonLock(LockName = "댓글좋아요", identifier = "commentId")
    @Transactional
    @InvalidateCacheByCommentId
    public void createCommentLike(Long commentId) {
        // 기존에 눌렀으면 취소 처리
        Long idpId = SecurityUtils.getCurrentUserId();
        Result<CommentLike> commentLikeResult =
                commentLikeLoadPort.getByCommentIdAndIdpId(commentId, idpId);
        Comment comment = commentLoadPort.findById(commentId);
        if (commentLikeResult.isSuccess()) {
            deleteCommentLike(comment, commentLikeResult.getValue());
        } else {
            createCommentLike(comment, idpId);
        }
    }

    private void deleteCommentLike(Comment comment, CommentLike commentLike) {
        comment.minusLikeCount();
        commentLikeRecordPort.deleteCommentLike(commentLike);
    }

    private void createCommentLike(Comment comment, Long idpId) {
        comment.plusLikeCount();
        CommentLike newCommentLike =
                CommentLike.builder().commentId(comment.getId()).idpId(idpId).build();
        commentLikeRecordPort.saveCommentLike(newCommentLike);
    }

    @Override
    @RedissonLock(LockName = "댓글좋아요", identifier = "commentId")
    @Transactional
    @InvalidateCacheByCommentId
    public void deleteCommentLike(Long commentId) {
        // 현재 내가 눌렀던 댓글만 삭제할 수 있다.
        Long idpId = SecurityUtils.getCurrentUserId();
        Comment comment = commentLoadPort.findById(commentId);
        Result<CommentLike> commentLikeResult =
                commentLikeLoadPort.getByCommentIdAndIdpId(commentId, idpId);
        commentLikeResult.onSuccess(
                commentLike -> {
                    commentLikeRecordPort.deleteCommentLike(commentLike);
                    comment.minusLikeCount();
                });
        commentLikeResult.onFailure(
                throwable -> {
                    throw CommentNotHostException.EXCEPTION;
                });
    }

    @Override
    public List<CommentPairVo> findByCardId(Long cardId) {
        Long idpId = SecurityUtils.getCurrentUserId();
        Card card = cardLoadPort.findById(cardId);
        List<Comment> comments = commentLoadPort.findByCardId(card.getId());

        return getCommentPairVo(idpId, comments);
    }

    @NotNull
    private List<CommentPairVo> getCommentPairVo(Long idpId, List<Comment> comments) {
        List<Long> idpIds = comments.stream().map(Comment::getIdpId).collect(Collectors.toList());

        List<Interviewer> interviewers = interviewerLoadPort.loadInterviewerByIdpIds(idpIds);
        List<CommentLike> commentLikes =
                commentLikeLoadPort.findByCommentIds(
                        comments.stream().map(Comment::getId).collect(Collectors.toList()));
        return comments.stream()
                .map(
                        comment -> {
                            Boolean isLiked =
                                    commentLikes.stream()
                                            .anyMatch(
                                                    commentLike ->
                                                            commentLike
                                                                            .getCommentId()
                                                                            .equals(comment.getId())
                                                                    && commentLike
                                                                            .getIdpId()
                                                                            .equals(idpId));

                            Boolean canEdit = Objects.equals(comment.getIdpId(), idpId);
                            String interviewersName =
                                    interviewers.stream()
                                            .filter(
                                                    interviewer ->
                                                            interviewer
                                                                    .getId()
                                                                    .equals(comment.getIdpId()))
                                            .findFirst()
                                            .map(Interviewer::getName)
                                            .orElse("");
                            return CommentPairVo.of(comment, isLiked, interviewersName, canEdit);
                        })
                .collect(Collectors.toList());
    }

    @Override
    public Boolean isCheckedLike(Long commentId) {
        Long idpId = SecurityUtils.getCurrentUserId();
        return commentLikeLoadPort.getByCommentIdAndIdpId(commentId, idpId).isSuccess();
    }

    @Override
    @Transactional
    @InvalidateCacheByCommentId
    public void updateCommentContent(Long commentId, Map<String, String> contents) {
        String content = contents.get("content");
        // 내가 작성한 comment 만 수정할 수 있다.
        Long idpId = SecurityUtils.getCurrentUserId();
        Comment comment = commentLoadPort.findById(commentId);

        validateHost(comment, idpId);
        comment.updateContent(content);
    }

    //
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "commentsByApplicantId", key = "#applicantId")
    public List<CommentPairVo> findByApplicantId(String applicantId) {
        Long idpId = SecurityUtils.getCurrentUserId();
        List<Comment> comments = commentLoadPort.findByApplicantId(applicantId);
        return getCommentPairVo(idpId, comments);
    }

    @Override
    @Transactional
    @InvalidateCacheByDeleteComment
    public void deleteCommentByCardId(Long cardId) {
        List<Comment> comments = commentLoadPort.findByCardId(cardId);
        if (comments.isEmpty()) {
            return;
        }
        commentRecordPort.deleteCommentsAll(comments);
        commentLikeRecordPort.deleteAll(
                commentLikeLoadPort.findByCommentIds(
                        comments.stream().map(Comment::getId).collect(Collectors.toList())));
    }
}
