package com.greenfoxacademy.reddit2.services;

import com.greenfoxacademy.reddit2.modells.Post;
import com.greenfoxacademy.reddit2.modells.User;
import com.greenfoxacademy.reddit2.repositories.PostRepository;
import com.greenfoxacademy.reddit2.viewmodells.PostDTO;
import org.springframework.stereotype.Component;

@Component
public class PostServiceImpl implements PostService {

    PostRepository postRepository;
    UserService userService;
    DateTimeFormatter dateTimeFormatter;

    public PostServiceImpl(PostRepository postRepository, UserService userService, DateTimeFormatter dateTimeFormatter) {
        this.postRepository = postRepository;
        this.userService = userService;
        this.dateTimeFormatter = dateTimeFormatter;
    }

    @Override
    public void saveNewPost(String title, String url) {
        User currentUser = this.userService.getCurrentUser();
        Post newPost = new Post(title, url, currentUser);
        this.postRepository.save(newPost);
    }

    @Override
    public PostDTO covertPostToPostDTO(Post post) {
        PostDTO postDTO = new PostDTO();
        postDTO.setTitle(post.getTitle());
        postDTO.setUrl(post.getUrl());
        postDTO.setDate(this.dateTimeFormatter.getDate(post.getCreated()));
        postDTO.setTime(this.dateTimeFormatter.getTime(post.getCreated()));
        postDTO.setScore(post.getScore());
        postDTO.setUserName(post.getUser().getUserName());
        if (post.getVote()==null) {
            postDTO.setTotalVotes(0);
        } else {
            postDTO.setTotalVotes(post.getVote().size());
        }
        return postDTO;
    }
}
