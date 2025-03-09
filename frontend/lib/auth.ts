import { jwtDecode } from 'jwt-decode';
import Cookies from 'js-cookie';

interface DecodedToken {
  email: string;
  role: string;
  exp: number;
}

export const getToken = (): string | null => {
  return Cookies.get('token') || null;
};

export const decodeToken = (token: string): DecodedToken | null => {
  try {
    return jwtDecode<DecodedToken>(token);
  } catch (error) {
    console.error('Invalid token:', error);
    return null;
  }
};

export const isLoggedIn = (token: string): boolean => {
  const decodedToken = decodeToken(token);
  if (!decodedToken) {
    return false;
  }

  return decodedToken.exp > Date.now() / 1000;
};

export const logout = () => {
  Cookies.remove('token');
  window.location.href = '/sign-in';
};

export const setToken = (token: string): void => {
  Cookies.set('token', token, {
    path: '/',
    expires: 10 / 24,
    secure: true,
    sameSite: 'strict',
  });
};
