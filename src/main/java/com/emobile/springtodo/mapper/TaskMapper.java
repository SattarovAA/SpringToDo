package com.emobile.springtodo.mapper;

import com.emobile.springtodo.model.dto.task.TaskInsertRequest;
import com.emobile.springtodo.model.dto.task.TaskListResponse;
import com.emobile.springtodo.model.dto.task.TaskResponse;
import com.emobile.springtodo.model.dto.task.TaskSaveRequest;
import com.emobile.springtodo.model.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Mapper for working with {@link Task} entity.
 *
 * @see TaskSaveRequest
 * @see TaskInsertRequest
 * @see TaskResponse
 * @see TaskListResponse
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TaskMapper {
    /**
     * {@link TaskSaveRequest} to {@link Task} mapping.
     *
     * @param request {@link TaskSaveRequest} for mapping.
     * @return mapped {@link Task}.
     */
    Task requestToModel(TaskSaveRequest request);

    /**
     * {@link TaskInsertRequest} to {@link Task} mapping.
     *
     * @param request {@link TaskInsertRequest} for mapping.
     * @return mapped {@link Task}.
     */
    Task requestToModel(TaskInsertRequest request);

    /**
     * {@link Task} to {@link TaskResponse} mapping.
     *
     * @param model {@link Task} for mapping.
     * @return mapped {@link TaskResponse}.
     */
    @Mapping(source = "author.id", target = "authorId")
    TaskResponse modelToResponse(Task model);


    @Named("OffsetDateTimeToString")
    default String toLocalDateTime(OffsetDateTime value) {
        return value.toString();
    }

    /**
     * List of {@link Task} to
     * content of {@link TaskResponse} mapping.
     *
     * @param modelList List of {@link Task} for mapping.
     * @return mapped List of {@link TaskResponse}.
     * @see #modelToResponse(Task)
     */
    List<TaskResponse> modelListToResponseList(List<Task> modelList);

    /**
     * List of {@link Task} to {@link TaskListResponse} mapping.
     *
     * @param modelList List of {@link Task} for mapping.
     * @return mapped {@link TaskListResponse}.
     * @see #modelListToResponseList(List)
     */
    default TaskListResponse modelListToModelListResponse(
            List<Task> modelList) {
        return new TaskListResponse(
                modelListToResponseList(modelList)
        );
    }
}
