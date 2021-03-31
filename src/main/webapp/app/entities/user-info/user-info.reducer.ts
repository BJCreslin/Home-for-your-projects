import axios from 'axios';
import { ICrudGetAction, ICrudGetAllAction, ICrudPutAction, ICrudDeleteAction } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IUserInfo, defaultValue } from 'app/shared/model/user-info.model';

export const ACTION_TYPES = {
  FETCH_USERINFO_LIST: 'userInfo/FETCH_USERINFO_LIST',
  FETCH_USERINFO: 'userInfo/FETCH_USERINFO',
  CREATE_USERINFO: 'userInfo/CREATE_USERINFO',
  UPDATE_USERINFO: 'userInfo/UPDATE_USERINFO',
  PARTIAL_UPDATE_USERINFO: 'userInfo/PARTIAL_UPDATE_USERINFO',
  DELETE_USERINFO: 'userInfo/DELETE_USERINFO',
  RESET: 'userInfo/RESET',
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IUserInfo>,
  entity: defaultValue,
  updating: false,
  updateSuccess: false,
};

export type UserInfoState = Readonly<typeof initialState>;

// Reducer

export default (state: UserInfoState = initialState, action): UserInfoState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_USERINFO_LIST):
    case REQUEST(ACTION_TYPES.FETCH_USERINFO):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true,
      };
    case REQUEST(ACTION_TYPES.CREATE_USERINFO):
    case REQUEST(ACTION_TYPES.UPDATE_USERINFO):
    case REQUEST(ACTION_TYPES.DELETE_USERINFO):
    case REQUEST(ACTION_TYPES.PARTIAL_UPDATE_USERINFO):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true,
      };
    case FAILURE(ACTION_TYPES.FETCH_USERINFO_LIST):
    case FAILURE(ACTION_TYPES.FETCH_USERINFO):
    case FAILURE(ACTION_TYPES.CREATE_USERINFO):
    case FAILURE(ACTION_TYPES.UPDATE_USERINFO):
    case FAILURE(ACTION_TYPES.PARTIAL_UPDATE_USERINFO):
    case FAILURE(ACTION_TYPES.DELETE_USERINFO):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload,
      };
    case SUCCESS(ACTION_TYPES.FETCH_USERINFO_LIST):
      return {
        ...state,
        loading: false,
        entities: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.FETCH_USERINFO):
      return {
        ...state,
        loading: false,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.CREATE_USERINFO):
    case SUCCESS(ACTION_TYPES.UPDATE_USERINFO):
    case SUCCESS(ACTION_TYPES.PARTIAL_UPDATE_USERINFO):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data,
      };
    case SUCCESS(ACTION_TYPES.DELETE_USERINFO):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {},
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState,
      };
    default:
      return state;
  }
};

const apiUrl = 'api/user-infos';

// Actions

export const getEntities: ICrudGetAllAction<IUserInfo> = (page, size, sort) => ({
  type: ACTION_TYPES.FETCH_USERINFO_LIST,
  payload: axios.get<IUserInfo>(`${apiUrl}?cacheBuster=${new Date().getTime()}`),
});

export const getEntity: ICrudGetAction<IUserInfo> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_USERINFO,
    payload: axios.get<IUserInfo>(requestUrl),
  };
};

export const createEntity: ICrudPutAction<IUserInfo> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_USERINFO,
    payload: axios.post(apiUrl, cleanEntity(entity)),
  });
  dispatch(getEntities());
  return result;
};

export const updateEntity: ICrudPutAction<IUserInfo> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_USERINFO,
    payload: axios.put(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const partialUpdate: ICrudPutAction<IUserInfo> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.PARTIAL_UPDATE_USERINFO,
    payload: axios.patch(`${apiUrl}/${entity.id}`, cleanEntity(entity)),
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<IUserInfo> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_USERINFO,
    payload: axios.delete(requestUrl),
  });
  dispatch(getEntities());
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET,
});
