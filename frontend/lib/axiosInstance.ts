import axios from 'axios';
import { getToken } from './auth';

const axiosInstance = axios.create({
  baseURL: '/api/v1/',
  timeout: 1000,
  headers: { 'Content-Type': 'application/json' },
});

axiosInstance.interceptors.request.use(
  function (config) {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  function (error) {
    return Promise.reject(error);
  },
);

axiosInstance.interceptors.response.use(
  (response) => {
    console.log('AXIOS INTERCEPTOR RESPONSE:', response.status);
    return response;
  },
  (error) => {
    console.log('AXIOS INTERCEPTOR ERROR:', error.response?.status);
    if (
      error.response &&
      (error.response.status === 403 || error.response.status === 409)
    ) {
      if (typeof window !== 'undefined') {
        window.location.href = '/trips';
      }
    }
    return Promise.reject(error);
  },
);

export default axiosInstance;
