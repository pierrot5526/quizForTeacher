package cnam.glg204.domain.Dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.transaction.Transactional;

import junit.framework.TestCase;

import org.hibernate.mapping.Set;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.junit.FixMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cnam.glg204.quiz.common.config.PersistenceJPAConfig;
import cnam.glg204.quiz.common.enums.AccountType;
import cnam.glg204.quiz.common.enums.SessionStatus;
import cnam.glg204.quiz.common.enums.SubscriberStatus;
import cnam.glg204.quiz.server.domain.cours.Cours;
import cnam.glg204.quiz.server.domain.cours.CoursDao;
import cnam.glg204.quiz.server.domain.cours.CoursDaoImpl;
import cnam.glg204.quiz.server.domain.questions.Question;
import cnam.glg204.quiz.server.domain.questions.QuestionDao;
import cnam.glg204.quiz.server.domain.questions.QuestionDaoImpl;
import cnam.glg204.quiz.server.domain.result.Result;
import cnam.glg204.quiz.server.domain.result.ResultDao;
import cnam.glg204.quiz.server.domain.result.ResultDaoImpl;
import cnam.glg204.quiz.server.domain.sequence.Sequence;
import cnam.glg204.quiz.server.domain.sequence.SequenceDao;
import cnam.glg204.quiz.server.domain.sequence.SequenceDaoImpl;
import cnam.glg204.quiz.server.domain.sessionquiz.SessionQuiz;
import cnam.glg204.quiz.server.domain.sessionquiz.SessionQuizDao;
import cnam.glg204.quiz.server.domain.sessionquiz.SessionQuizDaoImpl;
import cnam.glg204.quiz.server.domain.topic.Topic;
import cnam.glg204.quiz.server.domain.topic.TopicDao;
import cnam.glg204.quiz.server.domain.topic.TopicDaoImpl;
import cnam.glg204.quiz.server.domain.user.User;
import cnam.glg204.quiz.server.domain.user.UserDao;
import cnam.glg204.quiz.server.domain.user.UserDaoImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceJPAConfig.class,
		CoursDaoImpl.class, SessionQuizDaoImpl.class, UserDaoImpl.class,
		TopicDaoImpl.class, QuestionDaoImpl.class, SequenceDaoImpl.class ,ResultDaoImpl.class })

@Transactional(rollbackOn = Exception.class)
@Rollback(true)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestResultDao extends TestCase {
	@Autowired
	CoursDao coursDao;
	@Autowired
	SessionQuizDao sessionQuizDao;
	@Autowired
	TopicDao topicDao;
	@Autowired
	UserDao userDao;
	@Autowired
	QuestionDao questionDao;
	@Autowired
	SequenceDao sequenceDao;
	@Autowired
	ResultDao resultDao;

	@Test
	public void testAutoId() {
		User user = EntitiesCreator.createRandomUser(AccountType.PROFESSOR);
		userDao.save(user);
		Cours cours = EntitiesCreator.createRandomCours(true, user, null);
		coursDao.save(cours);
		Sequence sequence = EntitiesCreator.createRandomSequence(user, null);
		sequenceDao.save(sequence);
		SessionQuiz sessionQuiz = EntitiesCreator.createRandomSessionQuiz(
				SessionStatus.RUNNING, cours, sequence);	
		sessionQuizDao.save(sessionQuiz);
		User auditor = EntitiesCreator.createRandomUser(AccountType.PROFESSOR);
		userDao.save(auditor);
	
		Result result1  =  EntitiesCreator.createRandomResult(auditor, sessionQuiz);

		resultDao.save(result1);
		Result result2  =  EntitiesCreator.createRandomResult(auditor, sessionQuiz);
		resultDao.save(result2);
		
		assertNotNull(result1 .getId());
		assertNotNull(result2 .getId());
		assertTrue(result1 .getId()!= result2 .getId());

	}
	
	static Map <Integer, Question> questions =null;
	static Map  <User, SubscriberStatus> auditors=null;
	static SessionQuiz sessionQuiz=null;
	static int resultsNumber = 0;
	@Test
	public void testBInjectData() {
		User professor = EntitiesCreator.createRandomUser(AccountType.PROFESSOR);
		userDao.save( professor);
	    auditors = new HashMap <User, SubscriberStatus>();
		for (int i = 0; i < 20 ; i++ ){
			User auditor = EntitiesCreator.createRandomUser(AccountType.AUDITOR);
			userDao.save( auditor );
			auditors.put( auditor ,SubscriberStatus.ACCEPTED );
		}
		
		Cours cours = EntitiesCreator.createRandomCours(true, professor , auditors);
		coursDao.save(cours);
		
		Topic topic = EntitiesCreator.createRandomTopic(professor);
		topicDao.save(topic);
		
		questions = EntitiesCreator.createListOfQuestions(questionDao,30, topic);
		
		Sequence sequence = EntitiesCreator.createSequence("sequencecName", "ceci est une sequence", professor, questions);
		sequenceDao.save(sequence);
		
		sessionQuiz = EntitiesCreator.createRandomSessionQuiz(SessionStatus.RUNNING, cours, sequence);
	
		sessionQuizDao.save(sessionQuiz);		

		resultsNumber = resultDao.findResultsBySession(sessionQuiz.getId()).size();
		for (Map.Entry <Integer , Question> q : questions.entrySet()){
			int pos = q.getKey();
			Question question = q.getValue();
			int j = 0;
			for (Map.Entry <User, SubscriberStatus> a : auditors.entrySet()){
				
				
				User user = a.getKey();	
				Result result = new Result();
							
				result.setQuestionId(question.getId());
				result.setQuestion(question.getQuestion());		
				
				
				result.setAnswers(generateResult(question.getAnswers()));
				result.setObtainedPoints((int)( Math.random()*10 ));	
				result.setUser(user);
				result.setSessionQuiz(sessionQuiz);
				resultDao.save(result);
			}		
		}

		assertEquals(resultsNumber + 30 * 20 ,resultDao.findResultsBySession(sessionQuiz.getId()).size());
		
		for (Map.Entry <User, SubscriberStatus> a : auditors.entrySet()){
				User user = a.getKey();	
				assertEquals(30  ,resultDao.findResultsByUserAndSession(user.getId(), sessionQuiz.getId()).size());
			}		
		
		for (Map.Entry <Integer , Question> q : questions.entrySet()){
			int pos = q.getKey();
			Question question = q.getValue();
			assertEquals(20  ,resultDao.findResultsByQuestionAndSession(question.getId(), sessionQuiz.getId()).size());
			for (Map.Entry <User, SubscriberStatus> a : auditors.entrySet()){
				User user = a.getKey();	
				assertEquals(1  ,resultDao.findResultsByUserQuestionAndSession(user.getId(),question.getId(), sessionQuiz.getId()).size());			
			}
		}
	}
	
	private Map <String,boolean[]> generateResult (	Map <String,Boolean> toGiveResult){
		Map <String,boolean[]> results = new  HashMap <String,boolean[]> ();
		for (Map.Entry<String,Boolean> v : toGiveResult.entrySet() ){
			String key = v.getKey();
			boolean [] value ={v.getValue(),new Random().nextBoolean()};
			results.put(key, value);
		}
			
		return 	results;
	}
	
	private List <String>  getGivenAnswer(List<String> propositions ){	
		List <String> givenAnswers = new ArrayList<String>();
		for (String givenAnswer : propositions)
			if(Math.random()*10>5)
				 givenAnswers.add(givenAnswer );
		return givenAnswers;		
		
	}
		
}
