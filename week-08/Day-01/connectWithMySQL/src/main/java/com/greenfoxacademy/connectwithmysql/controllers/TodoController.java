package com.greenfoxacademy.connectwithmysql.controllers;

import com.greenfoxacademy.connectwithmysql.models.Assignee;
import com.greenfoxacademy.connectwithmysql.models.Todo;
import com.greenfoxacademy.connectwithmysql.repositories.AssigneeRepository;
import com.greenfoxacademy.connectwithmysql.repositories.TodoRepository;
import com.greenfoxacademy.connectwithmysql.services.AssigneeService;
import com.greenfoxacademy.connectwithmysql.services.TodoService;
import com.greenfoxacademy.connectwithmysql.services.TodoServiceImp;
import com.sun.org.apache.xpath.internal.objects.XString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class TodoController {

    private TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @Autowired
    private AssigneeService assigneeService;


    @GetMapping(value = {"/", "/list"})
    public String list(Model model) {
        List<Todo> list = new ArrayList<>();
        this.todoService.findAll().forEach(list::add);
        model.addAttribute("todos", list);
        return "todolist";
    }

    @GetMapping("/todo/add")
    public String gotoNewTodo(Model model) {
        model.addAttribute("assignees", this.assigneeService.getNameAndEmail());
        return "add";
    }

    @PostMapping("/todo/add")
    public String addNewTodo(@RequestParam String newtodo, @RequestParam String description, @RequestParam boolean urgent, Model model,
                             @RequestParam String assignee, @RequestParam String choose, @RequestParam String name, @RequestParam String email) {
        if (choose.equals("1")) {
            if (email == "") {
                email = "email not set";
            }
            if (this.assigneeService.checkAssigneeExists(name,email)) {
                model.addAttribute("exists", "Assignee with this e-mail already exists. Please set different e-mail or name");
                return "redirect:/todo/add";
            }
            this.assigneeService.addAssignee(name,email);
            String nameEmail = name + ", "+email;
            Long assigneeID = this.assigneeService.getIdByNameAndEmail(nameEmail);
            this.todoService.saveWithAssignee(newtodo, urgent, description, this.assigneeService.getById(assigneeID));
        } else if (choose.equals("2")) {
            Long assigneeID = this.assigneeService.getIdByNameAndEmail(assignee);
            this.todoService.saveWithAssignee(newtodo, urgent, description, this.assigneeService.getById(assigneeID));
        } else if (choose.equals("3")) {
            this.todoService.save(newtodo, urgent, description);
        }
        return "redirect:/list";
    }

    @GetMapping("{idOfTodo}/delete")
    public String deleteToDo(@PathVariable Long idOfTodo) {
        this.todoService.deleteById(idOfTodo);
        return "redirect:/list";
    }

    @GetMapping("{idOfTodo}/edit")
    public String editToDo(Model model, @PathVariable Long idOfTodo) {
        model.addAttribute("id", idOfTodo);
        model.addAttribute("toDoTitle", this.todoService.getById(idOfTodo).getTitle());
        model.addAttribute("urgent", this.todoService.getById(idOfTodo).isUrgent());
        model.addAttribute("done", this.todoService.getById(idOfTodo).isDone());
        model.addAttribute("description", this.todoService.getById(idOfTodo).getDescription());
        return "edit";
    }

    @PostMapping("/{idOfTodo}/edit")
    public String saveEditedToDo(@RequestParam String newtodo, @RequestParam boolean urgent, @RequestParam String date
            , @RequestParam boolean done, @PathVariable Long idOfTodo, @RequestParam String description, Model model) {
        if (newtodo == "") {
            newtodo = this.todoService.getById(idOfTodo).getTitle();
        }
        this.todoService.edit(idOfTodo, newtodo, urgent, done, description, date);
        return "redirect:/list";
    }

    @PostMapping("/search")
    public String search(@RequestParam String search, Model model) {
        model.addAttribute("todos", this.todoService.findSearch(search));
        return "todolist";
    }

    @GetMapping("/list/name=urgent")
    public String searchUrgent(Model model) {
        model.addAttribute("todos", this.todoService.getByUrgent(true));
        return "todolist";
    }

    @GetMapping("/list/name=done")
    public String searchDone(Model model) {
        model.addAttribute("todos", this.todoService.getByDone(true));
        return "todolist";
    }

}
