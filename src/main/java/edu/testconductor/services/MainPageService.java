package edu.testconductor.services;

import edu.testconductor.domain.*;
import edu.testconductor.repos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class MainPageService {

    @Autowired
    private UserRepo usersRepo;

    @Autowired
    private ExamsRepo examsRepo;

    @Autowired
    private GroupsRepo groupsRepo;

    @Autowired
    private ThemeRepo themeRepo;

    @Autowired
    private StudentSessionRepo sessionsRepo;

    @Value("${app.datetime.format}")
    private String dateTimeFormat;

    private static Logger logger = LoggerFactory.getLogger(MainPageService.class);

    public HashMap<String, Object> generateIndexPageParams(HashMap<String, Object> params){

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = usersRepo.findByUsername(username);

        ArrayList<Exam> exams = null;
        Iterable<Theme> themes = null;
        Iterable<StudentGroup> groups = null;

        if (params.keySet().contains("exams"))
            exams = (ArrayList<Exam>)params.get("exams");
        if (params.keySet().contains("themes"))
            themes = (Iterable<Theme>)params.get("themes");
        if (params.keySet().contains("groups"))
            groups = (Iterable<StudentGroup>)params.get("groups");

        if(user.isTeacher()) { //TEACHER
            if (exams == null) exams =  (ArrayList<Exam>)examsRepo.findAllByOrderByStartDateTimeDesc(); //(ArrayList<Exam>)examsRepo.findAll();
            if (themes == null) themes = themeRepo.findAllByOrderByNameAsc();
            if (groups == null) groups = groupsRepo.findAllByOrderByGroupNameAsc(); //groupsRepo.findAll();

            for(Exam exam : exams) {
                exam.numberOfSessions = sessionsRepo.getCountOfSessionsByExamID(exam);
                exam.numberOfFinishedSessions = sessionsRepo.getCountOfFinishedSessionsByExamID(exam);
                if(LocalDateTime.now().isAfter(LocalDateTime.parse(exam.getEndDateTime(), DateTimeFormatter.ofPattern(dateTimeFormat))))
                    exam.result = -1;
                else if(LocalDateTime.now().isAfter(LocalDateTime.parse(exam.getStartDateTime(), DateTimeFormatter.ofPattern(dateTimeFormat))))
                    exam.result = 0;
                else
                    exam.result = 1;
            }

        } else if(user.isAdmin()) { // ADMIN
            if (exams == null) exams = (ArrayList<Exam>)examsRepo.findAllByOrderByStartDateTimeDesc();
            if (themes == null) themes = themeRepo.findAllByOrderByNameAsc();
            if (groups == null)groups = groupsRepo.findAllByOrderByGroupNameAsc();

            for(Exam exam : exams) {
                exam.numberOfSessions = sessionsRepo.getCountOfSessionsByExamID(exam);
                exam.numberOfFinishedSessions = sessionsRepo.getCountOfFinishedSessionsByExamID(exam);
            }

        } else { //STUDENT
            String groupName = user.getGroupName();
            if(groupName.contains(Character.toString((char)160))) {
                logger.warn("Found byaka for: " + username);
                groupName = groupName.replace(Character.toString((char)160), " ");
                user.setGroupName(groupName);
                usersRepo.save(user);
            }
            StudentGroup group = groupsRepo.findByGroupName(groupName);
            //ArrayList<Long> processedExams = sessionRepo.findExamIDsByEmail(user.getEmail());
            //String listOfProcessedExams = processedExams.stream().map(Object::toString).collect(Collectors.joining(","));

            String namesFoExamsToSearch = group.getGroupName();
            exams = (ArrayList<Exam>) examsRepo.findAllByExamName(namesFoExamsToSearch);
            for(String name : StudentGroup.getSpecialGroupNames()){ // add REWORKs
                exams.addAll((ArrayList<Exam>)examsRepo.findAllByExamName(name));
            }

            ArrayList<StudentSession> sessions = sessionsRepo.findAllByEmail(user.getEmail());

            //filter exams
            for(Exam e : exams){
                    for (StudentSession s : sessions)
                        if (s.getExam().getId().equals(e.getId())) {
                            e.result = s.getResult();
                            break;
                        }
                if(LocalDateTime.now().isAfter(LocalDateTime.parse(e.getEndDateTime(), DateTimeFormatter.ofPattern(dateTimeFormat)))) {
                    if(e.getResult() == -2) // was not 'touched'
                        e.result = -3; // N/A
                    if(e.getResult() == -1)
                        e.result = 0;
                }

            }
//            for(Exam e : examsAll){
//                if(!processedExams.contains(e.getId()))
//                    exams.add(e);
//            }

            themes = themeRepo.findAllByOrderByNameAsc();
            groups = groupsRepo.findAllByOrderByGroupNameAsc();

        }

        params.put("groups", groups);
        params.put("themes", themes);
        params.put("exams", exams);

        return params;
    }
}
