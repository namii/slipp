<%@ tag language="java" pageEncoding="UTF-8"%><%@ tag body-content="empty" %><%@
taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%><%@
taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%><%@
taglib prefix="sec" uri="http://www.springframework.org/security/tags"%><%@
taglib prefix="sf" uri="http://slipp.net/functions"%><%@
taglib prefix="sl" uri="http://www.slipp.net/tags"%><%@
taglib prefix="slipp" tagdir="/WEB-INF/tags" %><%@
attribute name="each" required="true" rtexprvalue="true" type="net.slipp.domain.qna.Answer" description="답변"%><%@
attribute name="isBest" required="true" rtexprvalue="true" type="java.lang.Boolean" description="" %>

<article class="article<c:if test='${isBest}'> qna-best-comment</c:if>" <c:if test="${not isBest}">id="answer-${each.answerId}"</c:if>>
	<c:if test="${isBest}">
		<div class="qna-best-comment-label">
			<strong>BEST 의견</strong>
			<small><a href="#answer-${each.answerId}">원본위치로&darr;</a></small>
		</div>
	</c:if>
	<div class="article-header">
		<div class="article-header-thumb">
			<img src='${sf:stripHttp(each.writer.imageUrl)}' class="article-author-thumb" alt="" />
		</div>
		<div class="article-header-text">
			<a href="${sf:stripHttp(each.writer.url)}" class="article-author-name">${each.writer.userId}</a>
			<a href="#answer-${each.answerId}" class="article-header-time" title="퍼머링크">
				<fmt:formatDate value="${each.createdDate}" pattern="yyyy-MM-dd HH:mm" />
				<i class="icon-link"></i>
			</a>
		</div>
	</div>
	<div class="article-doc comment-doc">
		${sf:wiki(each.contents)}
	</div>
	<div class="article-value">
	</div>
	<div class="article-util">
		<ul class="article-util-list">
			<li>
				<a class="btn-like-article" href="/api/questions/${question.questionId}/answers/${each.answerId}/like" title="좋아요">
					<i class="foundicon-thumb-up"></i>
					<span class="like-article-txt">좋아요</span>
					<strong class="like-article-count">${each.sumLike}</strong>
				</a>
				<a class="btn-dislike-article" href="/api/questions/${question.questionId}/answers/${each.answerId}/dislike" title="싫어요">
					<i class="foundicon-thumb-down"></i>
					<span class="dislike-article-txt">싫어요</span>
					<strong class="dislike-article-count">${each.sumDislike}</strong>
				</a>
			</li>
			<c:if test="${sf:isWriter(each.writer, loginUser)}">
				<li>
					<a class="link-modify-article" href="/questions/${question.questionId}/answers/${each.answerId}/form">수정</a>
				</li>
				<li>
					<form class="form-delete" action="/questions/${question.questionId}/answers/${each.answerId}" method="POST">
						<input type="hidden" name="_method" value="DELETE" />
						<button type="submit" class="link-delete-article">삭제</button>
					</form>
				</li>
			</c:if>
			<sec:authorize access="hasRole('ROLE_USER')">
				<li>
					<button type="button" class="link-answer-article" data-answer-id="${each.answerId}" data-answer-user-id="@${each.writer.userId}">언급&darr;</button>
				</li>
			</sec:authorize>
		</ul>
	</div>
</article>
