package cnam.glg204.domain.Dao;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.transaction.Transactional;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import cnam.glg204.quiz.common.config.PersistenceJPAConfig;
import cnam.glg204.quiz.common.enums.AccountType;
import cnam.glg204.quiz.common.enums.SessionStatus;
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
		TopicDaoImpl.class, QuestionDaoImpl.class, SequenceDaoImpl.class })
@Transactional(rollbackOn = Exception.class)
@Rollback(true)
public class TestSessionQuizDao extends TestCase {
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

	@Test
	public void testAutoId() {
		User user = EntitiesCreator.createRandomUser(AccountType.PROFESSOR);
		userDao.save(user);
		Cours cours = EntitiesCreator.createRandomCours(true, user, null);
		coursDao.save(cours);
		Sequence sequence = EntitiesCreator.createRandomSequence(user, null);
		sequenceDao.save(sequence);
		SessionQuiz sessionQuiz1 = EntitiesCreator.createRandomSessionQuiz(
				SessionStatus.RUNNING, cours, sequence);
		SessionQuiz sessionQuiz2 = EntitiesCreator.createRandomSessionQuiz(
				SessionStatus.RUNNING, cours, sequence);
		sessionQuizDao.save(sessionQuiz1);
		sessionQuizDao.save(sessionQuiz2);

		assertNotNull(sessionQuiz1.getId());
		assertNotNull(sessionQuiz2.getId());
		assertTrue(sessionQuiz1.getId() != sessionQuiz2.getId());
	}

	@Test
	public void testChangeState() {
		int countSession = sessionQuizDao.findAll().size();

		User user = EntitiesCreator.createRandomUser(AccountType.PROFESSOR);
		;
		userDao.save(user);
		Cours cours = EntitiesCreator.createRandomCours(true, user, null);
		coursDao.save(cours);
		Topic topic = EntitiesCreator.createRandomTopic(user);
		topicDao.save(topic);
		Sequence sequence = EntitiesCreator.createRandomSequence(user,
				EntitiesCreator.createListOfQuestions(questionDao, 10, topic));
		sequenceDao.save(sequence);
		SessionQuiz sessionQuiz1 = EntitiesCreator.createRandomSessionQuiz(
				SessionStatus.RUNNING, cours, sequence);
		sessionQuizDao.save(sessionQuiz1);

		assertEquals(sessionQuizDao.find(sessionQuiz1.getId()).getStatus(),
				SessionStatus.RUNNING);

		SessionQuiz sessionQuiz2 = sessionQuizDao.find(sessionQuiz1.getId());
		sessionQuiz2.setStatus(SessionStatus.NOT_RUNNING);

		assertEquals(countSession + 1, sessionQuizDao.findAll().size());
		assertEquals(sessionQuizDao.find(sessionQuiz1.getId()).getStatus(),
				SessionStatus.NOT_RUNNING);

	}

	@Test
	public void testSequenceContain() {
		int countSession = sessionQuizDao.findAll().size();
		int suscriberCount = 25;
		int questionCount = 35;
		User user = EntitiesCreator.createRandomUser(AccountType.PROFESSOR);
		userDao.save(user);
		Cours cours = EntitiesCreator
				.createRandomCours(true, user, EntitiesCreator
						.createMapOfSubscribers(suscriberCount, userDao));
		coursDao.save(cours);
		Topic topic = EntitiesCreator.createRandomTopic(user);
		topicDao.save(topic);
		Sequence sequence = EntitiesCreator.createRandomSequence(user,
				EntitiesCreator.createListOfQuestions(questionDao,
						questionCount, topic));
		sequenceDao.save(sequence);
		SessionQuiz sessionQuiz1 = EntitiesCreator.createRandomSessionQuiz(
				SessionStatus.RUNNING, cours, sequence);
		sessionQuizDao.save(sessionQuiz1);

		assertEquals(suscriberCount, sessionQuizDao.find(sessionQuiz1.getId())
				.getCours().getSubscribers().size());
		assertEquals(questionCount, sessionQuizDao.find(sessionQuiz1.getId())
				.getSequence().getQuestions().size());
	}
	
	@Test
	public void testFindSessionsByCours() {
		
		Map<Topic, Integer> topics = new HashMap<Topic, Integer>();
		User professor = EntitiesCreator.createRandomUser();
		professor.setAccountType(AccountType.PROFESSOR);
		userDao.save(professor);
		
		Map<Integer, Question> mapQuestions = EntitiesCreator.createListOfQuestions(questionDao, topics);
		Sequence sequence = EntitiesCreator.createRandomSequence(professor, mapQuestions);
		sequenceDao.save( sequence );
		
		Cours cours = EntitiesCreator.createRandomCours(true, professor , null);
		coursDao.save(cours);
		
		
		for (int i = 0 ; i<10 ;i++){
			
			SessionQuiz sessionQuiz = EntitiesCreator.createRandomSessionQuiz(SessionStatus.RUNNING, cours, sequence);
			sessionQuizDao.save(sessionQuiz);
		}

		
		assertEquals(10,sessionQuizDao.findByCours(cours).size());
		
	}


}
