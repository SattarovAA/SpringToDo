package com.emobile.springtodo.mapper;

import com.emobile.springtodo.model.dto.user.UserInsertRequest;
import com.emobile.springtodo.model.dto.user.UserListResponse;
import com.emobile.springtodo.model.dto.user.UserResponse;
import com.emobile.springtodo.model.dto.user.UserResponseWith;
import com.emobile.springtodo.model.dto.user.UserSaveRequest;
import com.emobile.springtodo.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * Mapper for working with {@link User} entity.
 *
 * @see UserSaveRequest
 * @see UserInsertRequest
 * @see UserResponse
 * @see UserResponseWith
 * @see UserListResponse
 */
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = TaskMapper.class)
public interface UserMapper {
    /**
     * {@link UserSaveRequest} to {@link User} mapping.
     *
     * @param request {@link UserSaveRequest} for mapping.
     * @return mapped {@link User}.
     */
    User requestToModel(UserSaveRequest request);

    /**
     * {@link UserInsertRequest} to {@link User} mapping.
     *
     * @param request {@link UserInsertRequest} for mapping.
     * @return mapped {@link User}.
     */
    User requestToModel(UserInsertRequest request);

    /**
     * {@link User} to {@link UserResponse} mapping.
     *
     * @param model {@link User} for mapping.
     * @return mapped {@link UserResponse}.
     */
    UserResponse modelToResponse(User model);

    /**
     * {@link User} to {@link UserResponse} mapping.
     *
     * @param model {@link User} for mapping.
     * @return mapped {@link UserResponse}.
     */
    UserResponseWith modelToResponseWith(User model);

    /**
     * List of {@link User} to
     * content of {@link UserResponse} mapping.
     *
     * @param modelList List of {@link User} for mapping.
     * @return mapped List of {@link UserResponse}.
     * @see #modelToResponse(User)
     */
    List<UserResponse> modelListToResponseList(List<User> modelList);

    /**
     * List of {@link User} to {@link UserListResponse} mapping.
     *
     * @param modelList List of {@link User} for mapping.
     * @return mapped {@link UserListResponse}.
     * @see #modelListToResponseList(List)
     */
    default UserListResponse modelListToModelListResponse(
            List<User> modelList) {
        return new UserListResponse(
                modelListToResponseList(modelList)
        );
    }
}
