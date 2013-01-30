package net.slipp.service.tag;

import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.inject.Inject;

import net.slipp.domain.qna.Question;
import net.slipp.domain.tag.NewTag;
import net.slipp.domain.tag.Tag;
import net.slipp.domain.user.SocialUser;
import net.slipp.repository.tag.NewTagRepository;
import net.slipp.repository.tag.TagRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.google.common.collect.Sets;

@Service
@Transactional
public class TagService {
	private TagRepository tagRepository;
	private NewTagRepository newTagRepository;
	
	public TagService() {
	}
	
	@Inject
	public TagService(TagRepository tagRepository, NewTagRepository newTagRepository) {
		this.tagRepository = tagRepository;
		this.newTagRepository = newTagRepository;
	}
	
    public Set<Tag> processTags(String plainTags) {
    	Set<String> parsedTags = parseTags(plainTags);
    	Set<Tag> tags = Sets.newHashSet();
    	for (String each : parsedTags) {
            Tag tag = tagRepository.findByName(each);
            if (tag == null) {
                Tag newTag = Tag.newTag(each);
                Tag persisted = tagRepository.save(newTag);
                tags.add(persisted);
            } else {
                tags.add(tag);                
            }			
		}
    	return tags;
    }
    
    static Set<String> parseTags(String plainTags) {
        Set<String> parsedTags = Sets.newHashSet();
        StringTokenizer tokenizer = new StringTokenizer(plainTags, " |,");
        while (tokenizer.hasMoreTokens()) {
            parsedTags.add(tokenizer.nextToken());
        }
        return parsedTags;
    }
	
	public void saveNewTag(SocialUser loginUser, Question question, Set<NewTag> newTags) {
		for (NewTag newTag : newTags) {
			applyNewTag(loginUser, question, newTag);			
		}
	}

	private void applyNewTag(SocialUser loginUser, Question question, NewTag newTag) {
		NewTag originalTag = newTagRepository.findByName(newTag.getName());
		if(originalTag==null) {
			newTag.tagged(loginUser, question);
			newTagRepository.save(newTag);
		} else {
			originalTag.tagged(loginUser, question);
		}
	}

	public void moveToTag(Long newTagId, Long parentTagId) {
		Assert.notNull(newTagId, "이동할 tagId는 null이 될 수 없습니다.");
		
		NewTag newTag = newTagRepository.findOne(newTagId);
		Tag parentTag = findParentTag(parentTagId);
		Tag tag = saveTag(newTag.createTag(parentTag));
		newTag.moveToTag(tag);
	}

	private Tag findParentTag(Long parentTagId) {
		Tag parentTag = null;
		if (parentTagId != null) {
			parentTag = tagRepository.findOne(parentTagId);
		}
		return parentTag;
	}

	public Page<Tag> findTags(Pageable page) {
		return tagRepository.findAll(page);
	}
	
	public Iterable<Tag> findsTag() {
		return tagRepository.findParents();
	}
	
	public Page<NewTag> findNewTags(Pageable page) {
		return newTagRepository.findAll(page);
	}
	
	public Tag saveTag(Tag tag) {
		return tagRepository.save(tag);
	}
	
	public Tag findTagByName(String name) {
		return tagRepository.findByName(name);
	}
	
	public Tag findTagById(Long id) {
		return tagRepository.findOne(id);
	}
	
	public List<Tag> findsBySearch(String keyword) {
		return tagRepository.findByNameLike(keyword + "%");
	}
}
