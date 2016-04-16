package com.pfaraco.domain.Dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import junit.framework.TestCase;

import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.internal.EntityManagerFactoryImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.pfaraco.quiz.server.config.PersistenceJPAConfig;
import com.pfaraco.quiz.server.domain.cours.Cours;
import com.pfaraco.quiz.server.domain.questions.Question;
import com.pfaraco.quiz.server.domain.questions.QuestionDao;
import com.pfaraco.quiz.server.domain.questions.QuestionDaoImpl;
import com.pfaraco.quiz.server.domain.result.Result;
import com.pfaraco.quiz.server.domain.sequence.Sequence;
import com.pfaraco.quiz.server.domain.sessionquiz.SessionQuiz;
import com.pfaraco.quiz.server.domain.topic.Topic;
import com.pfaraco.quiz.server.domain.topic.TopicDao;
import com.pfaraco.quiz.server.domain.topic.TopicDaoImpl;
import com.pfaraco.quiz.server.domain.user.User;
import com.pfaraco.quiz.server.domain.user.UserDao;
import com.pfaraco.quiz.server.domain.user.UserDaoImpl;
import com.pfaraco.quiz.server.enums.AccountType;
import com.pfaraco.quiz.server.enums.SessionStatus;
import com.pfaraco.quiz.server.enums.SubscriberStatus;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { PersistenceJPAConfig.class,
		UserDaoImpl.class,TopicDaoImpl.class ,QuestionDaoImpl.class })

@Transactional
public class EntitiesCreator extends TestCase {
	@Test
	public void test(){
		assertTrue(true);		
	}

	public static User createRandomUser() {
		return createUser((int) (Math.random() * 10000) + "prenom",
				(int) (Math.random() * 10000) + "nom", "04-02-1981",
				(int) (Math.random() * 10000) + "nom@"
						+ (int) (Math.random() * 10000) + ".com",
				(int) (Math.random() * 10000) + " comentaire ",
				(int) (Math.random() * 10000) + "password",
				AccountType.PROFESSOR);
	}
	
	public static User createRandomUser(AccountType accountType) {
		return createUser((int) (Math.random() * 10000) + "prenom",
				(int) (Math.random() * 10000) + "nom", "04-02-1981",
				(int) (Math.random() * 10000) + "nom@"
						+ (int) (Math.random() * 10000) + ".com",
				(int) (Math.random() * 10000) + " comentaire ",
				(int) (Math.random() * 10000) + "password",
				accountType);
	}

	public static User createUser(String firstName, String lastName,
			String date, String email, String commentary, String password,
			AccountType accountType) {


		return new User(firstName, lastName, createRandomDate(), email, commentary,
				password, accountType);
	}
	
	public static Topic createRandomTopic(User user) {

		return createTopic((int) (Math.random() * 10000) + "name",
				(int) (Math.random() * 10000) + "description", user);
	}

	public static Topic createTopic(String topicName, String description,
			User user) {
		return new Topic(topicName, description, user);
	}
	
	
	public static Map<Integer, Question> createListOfQuestions(QuestionDao questionDao,int number , Topic topic){
		Map<Integer, Question> questions = new HashMap<Integer, Question>();
		for (int i = 0; i < number; i++) {
			questions.put(i, createRandomQuestion(topic));
			questionDao.save(questions.get(i));
		}
		return questions;
	}
	
	
	public static Question createRandomQuestion(Topic topic) {
		List<String> propositions = new ArrayList<String>();
		List<String> answers = new ArrayList<String>();
		for (int i=0;i<(int) (Math.random() * 10);i++){
			propositions.add("proposition "+ i);
			answers.add("answer "+ i);
			}
			
		
		return createQuestion((int) (Math.random() * 10000) + "question ?", 
				propositions,
				answers,
				(int) (Math.random() * 10),
				(int) (Math.random() * 60),
				(int) (Math.random() * 100),
				topic);
	}

	public static Question createQuestion(String question,List <String> propositions, List <String> answers,int points,int timeToAnswer,int position, Topic topic)
		{
		return new Question(question, propositions, answers, points,timeToAnswer, position, topic);
		}
	
	public static Sequence createRandomSequence(User user,Map<Integer, Question> questions) {

		return createSequence((int) (Math.random() * 10000) + "name",
				(int) (Math.random() * 10000) + "description", user,questions);
	}
	
	public static Sequence createSequence(String sequencecName, String description,
			User user,Map <Integer,Question> questions) {
		return new Sequence( sequencecName, description , user,questions);
	}
	
	public static Map <User,SubscriberStatus>  createMapOfSubscribers(int size ,UserDao userDao){
		Map <User,SubscriberStatus>  subscribers = new HashMap<User,SubscriberStatus> ();
		for (int i =0;i<size;i++){
			 User auditor = createRandomUser(AccountType.AUDITOR);
			 userDao.save(auditor);
			 subscribers.put(auditor, SubscriberStatus.WAITING_ANSWER);				
		}
		
		return subscribers ;	
	}
	
	
	public static Cours createRandomCours(Boolean active, User user,Map <User,SubscriberStatus> subscribers){
		
		return createCours((int) (Math.random() * 10000) + "nameCours", 
				(int) (Math.random() * 10000) + "descitptionCours", 
				active,
					user,subscribers);
	}
	
	public static Cours createCours(String name, String  description,boolean active,User user,Map <User,SubscriberStatus> subscribers){
		return new Cours(name, description, active, user, subscribers);
	}
	
	public static SessionQuiz createRandomSessionQuiz(SessionStatus status,Cours cours,Sequence sequence ){
	
		return createSessionQuiz(status,createRandomDate(),null,cours,sequence);				
	}

	public static SessionQuiz createSessionQuiz(SessionStatus status,Date startDate,Date endDate , Cours cours ,Sequence sequence ){
	
		return new SessionQuiz(status, startDate, endDate, cours, sequence);
	}
	
	public static Result createRandomResult(User auditor ,SessionQuiz sessionQuiz ){
		
		List<String> goodAnswers= new ArrayList<String> ();
		List<String> answers= new ArrayList<String> ();
		
		for (int i= 0 ; i<(int) (Math.random() * 10);i++){			
			goodAnswers.add(	i +") "+  (Math.random()*10000000));
		}
		for (int i= 0 ; i<(int) (Math.random() * 5);i++){			
			answers.add(	i +") "+  (Math.random()*10000000));
		}	
		
		return createResult((int) (Math.random() * 10)
					, (float)(Math.random()*10), 
						auditor, 
						sessionQuiz, 
						"Quel est le "+  (Math.random()*10000000)+" du "+ (Math.random()*10000000) + "?", 
						goodAnswers,
						answers,
						createRandomDate());
	}
	
	
	public static Result createResult(int point, float answerTime ,User auditor ,SessionQuiz sessionQuiz,String question,List<String>goodAnswers, List<String>answers,Date date ){
		
		return new Result(point, answerTime, auditor, sessionQuiz, question, goodAnswers, answers, date);
	}
	
	
	private static Date createRandomDate(){
		Date dateFormat = null;
		try {
			dateFormat = new SimpleDateFormat("dd-MM-yyyy").parse("04-02-1981");
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return  dateFormat 	;	
	}
}
