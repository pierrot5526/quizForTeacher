package com.cnam.quiz.server.service.quiz;

import java.util.List;

import com.cnam.quiz.common.dto.QuestionDto;
import com.cnam.quiz.common.dto.SequenceDto;
import com.cnam.quiz.common.dto.TopicDto;


public interface QuizService {
	
	   	TopicDto findTopic(long id) ;
	     	     
	    void createTopic(TopicDto topicDto);
	     
	    void updateTopic(TopicDto topicDto);
	     
	    void deleteTopic(long id)  ;
	 
	    List<TopicDto> findAllTopics() ;   
	    
	    List<TopicDto> findTopicsByProfessor(long userId) ;   
	    
	 	QuestionDto findQuestion(long id) ;
	     
	    void createQuestion(QuestionDto  questionDto);
	     
	    void updateQuestion(QuestionDto  questionDto);
	     
	    void deleteQuestion(long id)  ;
	 
	    List<QuestionDto> findQuestionsByTopic(long topicId) ;   
	     
	   	SequenceDto findSequence(long id) ;
	     
	    void createSequence(SequenceDto sequenceDto);
	     
	    void updateSequence(SequenceDto sequenceDto);
	     
	    void deleteSequence(long id)  ;
	    
	    List<SequenceDto> findSequenceByProfessor(long userId);   
	    
}
