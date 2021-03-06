package cnam.glg204.service;

import javax.transaction.Transactional;

import static org.junit.Assert.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import cnam.glg204.quiz.common.config.PersistenceJPAConfig;
import cnam.glg204.quiz.common.dto.CoursDto;
import cnam.glg204.quiz.common.dto.QuestionDto;
import cnam.glg204.quiz.common.dto.SequenceDto;
import cnam.glg204.quiz.common.dto.SequenceWithQuestionsDto;
import cnam.glg204.quiz.common.dto.SessionQuizDto;
import cnam.glg204.quiz.common.dto.TopicDto;
import cnam.glg204.quiz.common.enums.AccountType;
import cnam.glg204.quiz.common.enums.QuestionType;
import cnam.glg204.quiz.common.enums.SessionStatus;
import cnam.glg204.quiz.common.exceptions.CheckException;
import cnam.glg204.quiz.common.exceptions.CoursNotActiveException;
import cnam.glg204.quiz.common.exceptions.NoCoursSelectedException;
import cnam.glg204.quiz.common.exceptions.SessionQuizAlreadyRunningException;
import cnam.glg204.quiz.server.domain.cours.Cours;
import cnam.glg204.quiz.server.domain.cours.CoursDao;
import cnam.glg204.quiz.server.domain.cours.CoursDaoImpl;
import cnam.glg204.quiz.server.domain.questions.Question;
import cnam.glg204.quiz.server.domain.questions.QuestionDao;
import cnam.glg204.quiz.server.domain.questions.QuestionDaoImpl;
import cnam.glg204.quiz.server.domain.sequence.Sequence;
import cnam.glg204.quiz.server.domain.sequence.SequenceDao;
import cnam.glg204.quiz.server.domain.sequence.SequenceDaoImpl;
import cnam.glg204.quiz.server.domain.sessionquiz.SessionQuiz;
import cnam.glg204.quiz.server.domain.sessionquiz.SessionQuizDaoImpl;
import cnam.glg204.quiz.server.domain.topic.Topic;
import cnam.glg204.quiz.server.domain.topic.TopicDao;
import cnam.glg204.quiz.server.domain.topic.TopicDaoImpl;
import cnam.glg204.quiz.server.domain.user.User;
import cnam.glg204.quiz.server.domain.user.UserDao;
import cnam.glg204.quiz.server.domain.user.UserDaoImpl;
import cnam.glg204.quiz.server.service.quiz.QuizService;
import cnam.glg204.quiz.server.service.quiz.QuizServiceImpl;

import cnam.glg204.domain.Dao.EntitiesCreator;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceJPAConfig.class, UserDaoImpl.class, TopicDaoImpl.class,
		SequenceDaoImpl.class, QuestionDaoImpl.class, SessionQuizDaoImpl.class, 
		CoursDaoImpl.class,SequenceDaoImpl.class, QuizServiceImpl.class })

@Transactional(rollbackOn = Exception.class)
@Rollback(true)
public class TestQuizService {
	String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	@Autowired
	QuizService quizService;

	@Autowired
	UserDao userDao;

	@Autowired
	TopicDao topicDao;

	@Autowired
	QuestionDao questionDao;
	
	@Autowired
	CoursDao coursDao;
	
	@Autowired
	SequenceDao sequenceDao;

	@Test
	public void testCreateTopic() {

		TopicDto topicDto = createTopicDto();
		try {
			quizService.createTopic(topicDto);
		} catch (CheckException e) {
			fail();
		}

		long id = topicDto.getId();
		TopicDto topicDto2 = quizService.findTopic(id);

		assertNotNull(topicDto2);
		assertEquals(topicDto.getId(), topicDto2.getId());
		assertEquals(topicDto.getUserId(), topicDto2.getUserId());
		assertEquals(topicDto.getName(), topicDto2.getName());
		assertEquals(topicDto.getDescription(), topicDto2.getDescription());
	}

	@Test
	public void testUpdateTopic() {
		TopicDto topicDto = createTopicDto();
		try {
			quizService.createTopic(topicDto);
		} catch (CheckException e) {
			fail();
		}
		long id = topicDto.getId();
		TopicDto topicDto2 = quizService.findTopic(id);
		topicDto.setName("ceci est un topic");
		try {
			quizService.updateTopic(topicDto);
		} catch (CheckException e) {
			fail();
		}
		assertNotEquals(topicDto.getName(), topicDto2.getName());
		topicDto2 = quizService.findTopic(id);
		assertEquals(topicDto.getName(), topicDto2.getName());
	}

	@Test
	public void testDeleteTopic() {
		TopicDto topicDto = createTopicDto();
		try {
			quizService.createTopic(topicDto);
		} catch (CheckException e) {
			fail();
		}
		long id = topicDto.getId();
		int t = quizService.findAllTopics().size();
		quizService.deleteTopic(id);
		assertEquals(t - 1, quizService.findAllTopics().size());
	}

	@Test
	public void testListTopics() {

		int c = quizService.findAllTopics().size();
		User user1 = createListOfTopicDto(10);
		User user2 = createListOfTopicDto(15);
		List<TopicDto> topicsDto = quizService.findAllTopics();

		assertEquals(c + 25, topicsDto.size());

		topicsDto = quizService.findTopicsByProfessor(user1.getId());

		assertEquals(10, topicsDto.size());

		topicsDto = quizService.findTopicsByProfessor(user2.getId());

		assertEquals(15, topicsDto.size());
	}

	@Test
	public void testCRUDQuestion() {

		QuestionDto questionDto = createQuestionDto();
		try {
			quizService.createQuestion(questionDto);
		} catch (CheckException e) {
			fail();
		}
		long id = questionDto.getId();
		long topicId = questionDto.getTopicId();
		QuestionDto questionDto2 = quizService.findQuestion(id);

		assertNotNull(questionDto2);
		assertEquals(questionDto.getId(), questionDto2.getId());
		assertEquals(questionDto.getPosition(), questionDto2.getPosition());
		assertEquals(questionDto.getQuestion(), questionDto2.getQuestion());
		assertEquals(questionDto.getPoints(), questionDto2.getPoints());
		assertEquals(questionDto.getAnswers().size(), questionDto2.getAnswers().size());	
		assertEquals(questionDto.getQuestionType(), questionDto2.getQuestionType());
		assertEquals(questionDto.getTopicId(), questionDto2.getTopicId());

		questionDto.setQuestion("ceci est une question");
		try {
			quizService.updateQuestion(questionDto);
		} catch (CheckException e) {
			fail();
		}

		questionDto2 = null;
		questionDto2 = quizService.findQuestion(id);

		assertEquals("ceci est une question", questionDto2.getQuestion());
		int t = quizService.findQuestionsByTopic(topicId ).size();
		quizService.deleteQuestion(id);
		assertEquals(t -1 , quizService.findQuestionsByTopic(topicId ).size());
	}

	@Test
	public void testListQuestions() {
		User user = EntitiesCreator.createRandomUser();
		user.setAccountType(AccountType.PROFESSOR);
		userDao.save(user);
		Topic topic1 = createListOfQuestionDto(user, 10);
		Topic topic2 = createListOfQuestionDto(user, 15);
		Topic topic3 = createListOfQuestionDto(user, 0);

		List<QuestionDto> questionsDto = quizService.findQuestionsByTopic(topic1.getId());

		assertEquals(10, questionsDto.size());

		questionsDto = quizService.findQuestionsByTopic(topic2.getId());

		assertEquals(15, questionsDto.size());

		questionsDto = quizService.findQuestionsByTopic(topic3.getId());

		assertEquals(0, questionsDto.size());
	}


	@Test
	public void testCRUDSequence() {

		Map<Topic, Integer> topics = new HashMap<Topic, Integer>();
		User user = EntitiesCreator.createRandomUser();
		user.setAccountType(AccountType.PROFESSOR);
		userDao.save(user);
		Topic topic1 = EntitiesCreator.createRandomTopic(user);
		Topic topic2 = EntitiesCreator.createRandomTopic(user);
		Topic topic3 = EntitiesCreator.createRandomTopic(user);

		topics.put(topic1, 5);
		topicDao.save(topic1);
		topics.put(topic2, 5);
		topicDao.save(topic2);
		topics.put(topic3, 5);
		topicDao.save(topic3);
		Map<Integer, Question> mapQuestions = EntitiesCreator.createListOfQuestions(questionDao, topics);
		SequenceWithQuestionsDto sequenceDto = createSequenceWithQuestionsDto(user,mapQuestions);

		try {
			quizService.createSequence(sequenceDto);
		} catch (CheckException e1) {
			e1.printStackTrace();
		}

		long id = sequenceDto.getId();
		SequenceWithQuestionsDto sequenceDto2 = quizService.findSequence(id);

		assertNotNull(sequenceDto2);
		assertEquals(sequenceDto.getId(), sequenceDto.getId());
		assertEquals(sequenceDto.getName(), sequenceDto2.getName());
		assertEquals(sequenceDto.getDescription(), sequenceDto2.getDescription());
		assertEquals(15, sequenceDto2.getQuestions().size());
		Map<Integer, QuestionDto> questionsDto = sequenceDto.getQuestions();
		Map<Integer, QuestionDto> questionsDto2 = sequenceDto2.getQuestions();
		for (Map.Entry<Integer, QuestionDto> e : questionsDto.entrySet()) {
			Integer key = e.getKey();
			assertNotNull(questionsDto2.get(key));
			assertEquals(e.getValue().getId(), questionsDto2.get(key).getId());
			assertEquals(e.getValue().getPosition(), questionsDto2.get(key).getPosition());
			assertEquals(e.getValue().getQuestion(), questionsDto2.get(key).getQuestion());
			assertEquals(e.getValue().getPoints(), questionsDto2.get(key).getPoints());
			assertEquals(e.getValue().getAnswers().size(), questionsDto2.get(key).getAnswers().size());
			assertEquals(e.getValue().getQuestionType(), questionsDto2.get(key).getQuestionType());
			assertEquals(e.getValue().getTopicId(), questionsDto2.get(key).getTopicId());
		}

		assertEquals(sequenceDto.getUserId(), sequenceDto2.getUserId());

		sequenceDto.setName("ceci est une sequence");

		try {
			quizService.updateSequence(sequenceDto);
		} catch (CheckException e1) {
			e1.printStackTrace();
		}
		sequenceDto2 = null;
		sequenceDto2 = quizService.findSequence(id);

		assertEquals("ceci est une sequence", sequenceDto2.getName());

		int c = quizService.findSequenceByProfessor(user.getId()).size();
		quizService.deleteSequence(id);
		assertEquals(c - 1, quizService.findSequenceByProfessor(user.getId()).size());
	}
	

	@Test
	public void testListSequences() {
		Map<Topic, Integer> topics = new HashMap<Topic, Integer>();
		User user = EntitiesCreator.createRandomUser();
		user.setAccountType(AccountType.PROFESSOR);
		userDao.save(user);
		
		Topic topic1 = EntitiesCreator.createRandomTopic(user);
		Topic topic2 = EntitiesCreator.createRandomTopic(user);
		Topic topic3 = EntitiesCreator.createRandomTopic(user);
		
		topicDao.save(topic1);		
		topicDao.save(topic2);	
		topicDao.save(topic3);
		
		topics.put(topic1,10);
		topics.put(topic2,20);
		topics.put(topic3,30);
		
		Map<Integer, Question> mapQuestions = EntitiesCreator.createListOfQuestions(questionDao, topics);
		
		for (int i =0 ;i<50;i++){
	
			SequenceWithQuestionsDto sequenceDto = createSequenceWithQuestionsDto(user,mapQuestions);
			try {
				quizService.createSequence(sequenceDto);
			} catch (CheckException e) {
				fail();
			}	

		}	
		
		assertEquals(50 , quizService.findSequenceByProfessor(user.getId()).size());		

	
		
		assertEquals(10 , quizService.findQuestionsByTopic(topic1.getId()).size());
		assertEquals(20 , quizService.findQuestionsByTopic(topic2.getId()).size());
		assertEquals(30 , quizService.findQuestionsByTopic(topic3.getId()).size());

	}

	@Test
	public void testAddAndRemoveQuestionToSequence() {

		User user = EntitiesCreator.createRandomUser();
		user.setAccountType(AccountType.PROFESSOR);
		userDao.save(user);
		Topic topic1 = EntitiesCreator.createRandomTopic(user);
		Topic topic2 = EntitiesCreator.createRandomTopic(user);
		topicDao.save(topic1);		
		topicDao.save(topic2);	
		SequenceWithQuestionsDto sequenceDto = createSequenceWithQuestionsDto(user, null);
		
		try {
			quizService.createSequence(sequenceDto);
		} catch (CheckException e) {
			fail();
		}
		long id = sequenceDto.getId();
		
		for (int i = 0; i<12 ; i++){	
			Question question = EntitiesCreator.createRandomQuestion(topic1 , QuestionType.QUIZ);
			questionDao.save(question);
			try {
				quizService.addQuestionToSequence(sequenceDto.getId() , question.getId(),1);
			} catch (CheckException e) {
				fail();
			}
		}
		SequenceWithQuestionsDto sequenceDto2 = quizService.findSequence(id);
		assertEquals(12 ,sequenceDto2.getQuestions().size());
			
		Map<Integer, QuestionDto> questions = sequenceDto2.getQuestions();
	
		
		try {
			quizService.removeQuestionFromSequence( id , 4 );
		} catch (CheckException e) {
			fail();
		}
		
		sequenceDto2 = quizService.findSequence(id);
		assertEquals(11 ,sequenceDto2.getQuestions().size());
	
		sequenceDto = null ;
		sequenceDto2 = null;
		
		Map<Topic, Integer> topics = new HashMap<Topic, Integer>();
		topics.put(topic1,10);
		topics.put(topic2,20);	
		Map<Integer, Question> mapQuestions = EntitiesCreator.createListOfQuestions(questionDao, topics);
		sequenceDto = createSequenceWithQuestionsDto(user,mapQuestions);
		try {
			quizService.createSequence(sequenceDto);
		} catch (CheckException e) {
			fail();
		}
		id = sequenceDto.getId();
	    sequenceDto2 = quizService.findSequence(id);
		assertEquals(sequenceDto.getName() , sequenceDto2.getName());
			
		Question question = EntitiesCreator.createRandomQuestion(topic1 , QuestionType.QUIZ);
		questionDao.save(question);
		Question question2 = EntitiesCreator.createRandomQuestion(topic2 , QuestionType.QUIZ);
		questionDao.save(question2);
		try {
			quizService.addQuestionToSequence(sequenceDto.getId() , question.getId(),1);
		} catch (CheckException e) {
			fail();
		}
		try {
			quizService.addQuestionToSequence(sequenceDto.getId() , question2.getId(),1);
		} catch (CheckException e) {
			fail();
		}
		
		sequenceDto2 = null ; 
		
		sequenceDto2 = quizService.findSequence(id);
			
		assertEquals(10 + 20 + 1 + 1  ,sequenceDto2.getQuestions().size());
	}
//	*/
	
	@Test
	public void testCRUDSession() 
	{	
		Map<Topic, Integer> topics = new HashMap<Topic, Integer>();
		User professor = EntitiesCreator.createRandomUser();
		professor.setAccountType(AccountType.PROFESSOR);
		userDao.save(professor);
		
		Topic topic1 = EntitiesCreator.createRandomTopic(professor);
		Topic topic2 = EntitiesCreator.createRandomTopic(professor);
		Topic topic3 = EntitiesCreator.createRandomTopic(professor);
		
		topicDao.save(topic1);		
		topicDao.save(topic2);	
		topicDao.save(topic3);
		
		topics.put(topic1,10);
		topics.put(topic2,20);
		topics.put(topic3,30);
		
		Map<Integer, Question> mapQuestions = EntitiesCreator.createListOfQuestions(questionDao, topics);
		Sequence sequence = EntitiesCreator.createRandomSequence(professor, mapQuestions);
		sequenceDao.save( sequence );
		
		Cours cours = EntitiesCreator.createRandomCours(true, professor , null);
		coursDao.save(cours);
		
		SessionQuiz sessionQuiz = EntitiesCreator.createRandomSessionQuiz(SessionStatus.RUNNING, cours, sequence);
		SessionQuizDto sessionQuizDto = sessionQuizToSessionQuizDto ( sessionQuiz ) ; 
		try {
			try {
				quizService.startSessionQuiz(sessionQuizDto);
			} catch (CheckException | NoCoursSelectedException e) {
				fail();
			}
		} catch (SessionQuizAlreadyRunningException e) {
			fail();
		} catch (CoursNotActiveException e) {
			fail();
		}
		long id = sessionQuizDto.getId();
		
		SessionQuizDto sessionQuizDto2 = quizService.findSessionQuiz(id);
		
	//	assertEquals( sessionQuizDto.getStartDate(),sessionQuizDto2.getStartDate());
	//	assertEquals( sessionQuizDto.getEndDate(),sessionQuizDto2.getEndDate());	
		assertEquals( cours.getId(),sessionQuizDto2.getCoursId());
		assertEquals( sequence.getId(),sessionQuizDto2.getSequenceId());
		assertEquals( SessionStatus.RUNNING , sessionQuizDto2.getStatus());

		sessionQuizDto.setStatus(SessionStatus.NOT_RUNNING);
		try {
			quizService.stopSessionQuiz(sessionQuizDto);
		} catch (CheckException e) {
			fail();
		}
		
		sessionQuizDto2 = quizService.findSessionQuiz(id);
		assertEquals( SessionStatus.NOT_RUNNING , sessionQuizDto2.getStatus());
		
		int c = quizService.findSessionQuizByCours(cours.getId()).size();		
		quizService.deleteSessionQuiz(id);	
		assertEquals( c-1 , quizService.findSessionQuizByCours(cours.getId()).size());
	}
	
	
	private User createListOfTopicDto(int t) {
		User user = EntitiesCreator.createRandomUser();
		user.setAccountType(AccountType.PROFESSOR);
		userDao.save(user);
		for (int i = 0; i < t; i++)
			try {
				quizService.createTopic(createTopicDto(user));
			} catch (CheckException e) {
				fail();
			}
		return user;
	}

	private TopicDto createTopicDto() {
		User user = EntitiesCreator.createRandomUser();
		user.setAccountType(AccountType.PROFESSOR);
		userDao.save(user);
		return createTopicDto(user);
	}

	private TopicDto createTopicDto(User user) {
		Topic topic = EntitiesCreator.createRandomTopic(user);
		TopicDto topicDto = new TopicDto();
		topicDto.setName(topic.getName());
		topicDto.setDescription(topic.getDescription());
		topicDto.setUserId(topic.getUser().getId());
		return topicDto;
	}

	private Topic createListOfQuestionDto(User user, int t) {
		Topic topic = EntitiesCreator.createRandomTopic(user);
		topicDao.save(topic);
		for (int i = 0; i < t; i++)
			try {
				quizService.createQuestion(createQuestionDto(topic));
			} catch (CheckException e) {
				fail();
			}
		return topic;
	}

	private QuestionDto createQuestionDto() {
		User user = EntitiesCreator.createRandomUser();
		user.setAccountType(AccountType.PROFESSOR);
		userDao.save(user);
		Topic topic = EntitiesCreator.createRandomTopic(user);
		topicDao.save(topic);
		return createQuestionDto(topic);
	}

	private QuestionDto createQuestionDto(Topic topic) {
		Question question = EntitiesCreator.createRandomQuestion(topic, QuestionType.QUIZ);
		QuestionDto questionDto = questionToQuestionDto(question);
		return questionDto;
	}

	public QuestionDto questionToQuestionDto(Question question) {
		QuestionDto questionDto = new QuestionDto();
		questionDto.setId(question.getId());
		long topicId = question.getTopic().getId();
		questionDto.setTopicId(topicId);
		questionDto.setTitle(question.getTitle());
		questionDto.setPoints(question.getPoints());
		questionDto.setPosition(question.getPosition());
		questionDto.setAnswers(question.getAnswers());
		questionDto.setQuestion(question.getQuestion());
		questionDto.setQuestionType(question.getQuestionType());
		questionDto.setAnswers( question.getAnswers());
		questionDto.setTimeToAnswer(question.getTimeToAnswer());
		return questionDto;
	}

		

	private SequenceDto createSequenceDto( User user, Map<Integer, Question> mapQuestions ) {	
		Sequence sequence = EntitiesCreator.createRandomSequence(user, mapQuestions);
		SequenceDto sequenceDto = new SequenceDto();
		sequenceDto.setId(sequence.getId());
		long userId = sequence.getUser().getId();
		sequenceDto.setUserId(userId);
		sequenceDto.setName(sequence.getName());
		sequenceDto.setDescription(sequence.getDescription());
		return sequenceDto;
	}
	
	
	private SequenceWithQuestionsDto createSequenceWithQuestionsDto( User user, Map<Integer, Question> mapQuestions ) {	
		Sequence sequence = EntitiesCreator.createRandomSequence(user, mapQuestions);
		SequenceWithQuestionsDto sequenceDto = new SequenceWithQuestionsDto();
		sequenceDto.setId(sequence.getId());
		long userId = sequence.getUser().getId();
		sequenceDto.setUserId(userId);
		sequenceDto.setName(sequence.getName());
		sequenceDto.setDescription(sequence.getDescription());
		Map<Integer, Question> questions = sequence.getQuestions();
		Map<Integer, QuestionDto> questionsDto = new HashMap<Integer, QuestionDto>();
		int i = 0;
		if (questions != null && questions.size() != 0)
			for (Map.Entry<Integer, Question> e : questions.entrySet()) {
				Integer key = e.getKey();
				QuestionDto questionDto = this.questionToQuestionDto(e.getValue());
				questionsDto.put(key, questionDto);
			}
		sequenceDto.setQuestions(questionsDto);
		return sequenceDto;
	}
	
	
	public CoursDto coursToCoursDto(Cours cours) {
		CoursDto coursDto = new CoursDto();
		coursDto.setId(cours.getId());
		coursDto.setActive(cours.isActive());
		coursDto.setDescription(cours.getDescription());
		coursDto.setName(cours.getName());
		long userId = cours.getUser().getId();
		coursDto.setUserId(userId);
		return coursDto;
	}
	
	public SessionQuizDto  sessionQuizToSessionQuizDto (SessionQuiz sessionQuiz){
		SessionQuizDto sessionQuizDto = new SessionQuizDto();
		sessionQuizDto.setCoursId( sessionQuiz.getCours().getId());
		sessionQuizDto.setSequenceId(sessionQuiz.getSequence().getId());
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT );
		sessionQuizDto.setStartDate(formatter.format(sessionQuiz.getStartDate()));
		if (sessionQuiz.getEndDate() != null)
			sessionQuizDto.setEndDate(formatter.format(sessionQuiz.getEndDate()));
		sessionQuizDto.setStatus(sessionQuiz.getStatus());
		return sessionQuizDto;		
	}
}
