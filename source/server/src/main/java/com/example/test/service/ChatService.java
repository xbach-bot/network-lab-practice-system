package com.example.test.service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.example.test.core.error.BadRequestException;
import com.example.test.domain.Chat;
import com.example.test.domain.Room;
import com.example.test.domain.User;
import com.example.test.domain.request.chat.CreateChatDTO;
import com.example.test.domain.response.ResponseMetaDTO;
import com.example.test.domain.response.ResponsePaginationDTO;
import com.example.test.domain.response.chat.ResponseChatDTO;
import com.example.test.repository.ChatRepository;
import com.example.test.repository.UserRepository;

@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final RoomService roomService;
    private final ModelMapper modelMapper;

    public ChatService(ChatRepository chatRepository, UserRepository userRepository, RoomService roomService,
            ModelMapper modelMapper) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.roomService = roomService;
        this.modelMapper = modelMapper;
    }

    public ResponseChatDTO createChat(CreateChatDTO createChatDTO) throws BadRequestException {
        if (createChatDTO.getContent() == null) {
            throw new BadRequestException("Content is required");
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email);
        Room room = null;
        if (createChatDTO.getRoomId() != null) {

            room = roomService.getRoomById(createChatDTO.getRoomId());
            if (room == null) {
                throw new BadRequestException("Room not found");
            }

            if (room.getParticipants() != null && !room.getParticipants().contains(user)) {
                throw new BadRequestException("You are not a participant of this room");
            }
        }
        Chat chat = new Chat();
        chat.setContent(createChatDTO.getContent());
        chat.setUser(user);
        chat.setRoom(room);

        ResponseChatDTO responseChatDTO = modelMapper.map(chat, ResponseChatDTO.class);
        responseChatDTO.setCreatedAt(Instant.now());
        responseChatDTO.setUpdatedAt(Instant.now());

        chatRepository.save(chat);

        return responseChatDTO;
    }

    public ResponsePaginationDTO getChats(Specification<Chat> spec, Pageable pageable, Boolean lastPage)
            throws BadRequestException {
        Specification<Chat> finalSpec;

        int pageSize = (pageable != null) ? pageable.getPageSize() : 50;

        
        finalSpec = spec.and((root, query, cb) -> cb.isNull(root.get("room")));

        Page<Chat> chatPage;
        if (Boolean.TRUE.equals(lastPage)) {
            
            Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
            Pageable sortedPageable = PageRequest.of(0, pageSize, sort);

            Page<Chat> firstPage = this.chatRepository.findAll(finalSpec, sortedPageable);
            int totalPages = firstPage.getTotalPages();

            if (totalPages == 0) {
                chatPage = Page.empty();
            } else {
                Pageable lastPageable = PageRequest.of(totalPages - 1, pageSize, sort);
                chatPage = this.chatRepository.findAll(finalSpec, lastPageable);
            }
        } else {
            if (pageable == null) {
                pageable = PageRequest.of(0, pageSize);
            }
            chatPage = this.chatRepository.findAll(finalSpec, pageable);
        }

        ResponsePaginationDTO resultPaginationDTO = new ResponsePaginationDTO();
        ResponseMetaDTO meta = new ResponseMetaDTO();

        meta.setCurrent(chatPage.getNumber() + 1);
        meta.setPageSize(chatPage.getSize());
        meta.setPages(chatPage.getTotalPages());
        meta.setTotal(chatPage.getTotalElements());

        List<ResponseChatDTO> chats = chatPage.getContent().stream()
                .map(chatEntity -> modelMapper.map(chatEntity, ResponseChatDTO.class))
                .collect(Collectors.toList());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(chats);

        return resultPaginationDTO;
    }

    public ResponsePaginationDTO getChatsFromRoom(Specification<Chat> spec, Pageable pageable, Boolean lastPage,
            Long roomId)
            throws BadRequestException {
        Specification<Chat> finalSpec;

        int pageSize = (pageable != null) ? pageable.getPageSize() : 50;

        if (roomId != null) {
            
            Room room = roomService.getRoomById(roomId);
            if (room == null) {
                throw new BadRequestException("Room not found");
            }

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println("Authenticated user email: " + email);
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new BadRequestException("User not found");
            }

            if (room.getParticipants() == null || !room.getParticipants().contains(user)) {
                throw new BadRequestException("You are not a participant of this room");
            }

            
            Specification<Chat> roomEqSpec = (root, query, cb) -> cb.equal(root.get("room").get("id"), roomId);
            finalSpec = (spec != null) ? spec.and(roomEqSpec) : roomEqSpec;
        } else {
           
            Specification<Chat> roomNullSpec = (root, query, cb) -> cb.isNull(root.get("room").get("id"));
            finalSpec = (spec != null) ? spec.and(roomNullSpec) : roomNullSpec;
        }

        Page<Chat> chatPage;
        if (Boolean.TRUE.equals(lastPage)) {
            
            Sort sort = Sort.by(Sort.Direction.ASC, "createdAt");
            Pageable sortedPageable = PageRequest.of(0, pageSize, sort);

            Page<Chat> firstPage = this.chatRepository.findAll(finalSpec, sortedPageable);
            int totalPages = firstPage.getTotalPages();

            if (totalPages == 0) {
                chatPage = Page.empty();
            } else {
                Pageable lastPageable = PageRequest.of(totalPages - 1, pageSize, sort);
                chatPage = this.chatRepository.findAll(finalSpec, lastPageable);
            }
        } else {
            if (pageable == null) {
                pageable = PageRequest.of(0, pageSize);
            }
            chatPage = this.chatRepository.findAll(finalSpec, pageable);
        }

        ResponsePaginationDTO resultPaginationDTO = new ResponsePaginationDTO();
        ResponseMetaDTO meta = new ResponseMetaDTO();

        meta.setCurrent(chatPage.getNumber() + 1);
        meta.setPageSize(chatPage.getSize());
        meta.setPages(chatPage.getTotalPages());
        meta.setTotal(chatPage.getTotalElements());

        List<ResponseChatDTO> chats = chatPage.getContent().stream()
                .map(chatEntity -> modelMapper.map(chatEntity, ResponseChatDTO.class))
                .collect(Collectors.toList());

        resultPaginationDTO.setMeta(meta);
        resultPaginationDTO.setResult(chats);

        return resultPaginationDTO;
    }

    public ResponseChatDTO delete(Long id) throws BadRequestException {
        Chat chat = chatRepository.findById(id).orElseThrow(() -> new BadRequestException("message not found"));

        User user = userRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        if (!chat.getUser().equals(user)) {
            throw new BadRequestException("You are not allowed to delete this chat");
        }

        chatRepository.delete(chat);

        return modelMapper.map(chat, ResponseChatDTO.class);
    }
}
