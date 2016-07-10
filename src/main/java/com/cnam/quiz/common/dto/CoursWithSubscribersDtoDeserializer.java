package com.cnam.quiz.common.dto;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.context.annotation.Configuration;

import com.cnam.quiz.common.dto.CoursDto;
import com.cnam.quiz.common.enums.SubscriberStatus;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class CoursWithSubscribersDtoDeserializer extends JsonDeserializer<CoursWithSubscribersDto> {
	private static final ObjectMapper mapper = new ObjectMapper();
	@Override
	public CoursWithSubscribersDto deserialize(JsonParser jsonParser, DeserializationContext arg1)
			throws IOException, JsonProcessingException {
	
		ObjectCodec oc = jsonParser.getCodec();
		JsonNode node = oc.readTree(jsonParser);

		CoursWithSubscribersDto coursDto = new CoursWithSubscribersDto();
        long id = 0;
        if (node.get("id")!=null)
                 id = node.get("id").asLong();
		String name = node.get("name").textValue();
		String description = node.get("description").textValue();
		long userId = node.get("userId").asLong();
		boolean active = node.get("active").asBoolean();
				
		coursDto.setId(id);
		coursDto.setName(name);
		coursDto.setDescription(description);
		coursDto.setUserId(userId);
		coursDto.setActive(active);
		
		HashMap <UserDto,SubscriberStatus> subscribers =  new HashMap <UserDto,SubscriberStatus>();	
		JsonNode nodeAuthors = node.get("subscribers");
		while(nodeAuthors.elements().hasNext()){
			JsonNode suscriberNode = nodeAuthors.elements().next();
			System.out.println(suscriberNode);
		    long auditorId = suscriberNode.get("id").asLong();
			UserDto auditorDto = new UserDto();
			auditorDto.setId(auditorId);
			SubscriberStatus status = SubscriberStatus.valueOf(suscriberNode.get("status").textValue());
			subscribers.put(auditorDto , status);
		}
		
	
		
		coursDto.setSubscribers(subscribers);
		return coursDto;
	}

}