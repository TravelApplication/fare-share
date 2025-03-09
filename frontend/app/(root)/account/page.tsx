'use client';
import React from 'react';
import { CircleAlert } from 'lucide-react';
import { useEffect, useState } from 'react';
import axios from 'axios';
import { getToken, logout, setToken } from '@/lib/auth';
import { Button } from '@/components/ui/button';
import { Formik, Field, Form, ErrorMessage, FormikHelpers } from 'formik';
import { Input } from '@/components/ui/input';
import { z } from 'zod';
import { toFormikValidationSchema } from 'zod-formik-adapter';
import {
  updateEmailSchema,
  updatePasswordSchema,
} from '@/validation/updateUserSchema';
import { Alert } from '@/components/ui/alert';
import { User, UserSchema } from '@/validation/userProfileSchemas';
import { authApiSchema } from '@/validation/authSchemas';

function Page() {
  const [user, setUser] = useState<User | null>(null);
  const [passwordError, setPasswordError] = useState<string | null>(null);
  const [emailError, setEmailError] = useState<string | null>(null);
  const [isEditingEmail, setIsEditingEmail] = useState(false);
  const [isEditingPassword, setIsEditingPassword] = useState(false);

  const fetchUserData = async () => {
    try {
      const token = getToken();
      if (!token) {
        logout();
        return;
      }
      const response = await axios.get('/api/v1/users', {
        headers: { Authorization: `Bearer ${token}` },
      });
      const parsedUser = UserSchema.parse(response.data);
      setUser(parsedUser);
    } catch {
      logout();
    }
  };
  useEffect(() => {
    fetchUserData();
  }, []);

  const handleEmailUpdate = async (
    values: z.infer<typeof updateEmailSchema>,
    { resetForm }: FormikHelpers<z.infer<typeof updateEmailSchema>>,
  ) => {
    try {
      console.log(values.newEmail, user?.email, values.currentPassword);
      const authResponse = await axios.post(
        'http://localhost:8080/auth/login',
        { email: user?.email, password: values.currentPassword },
      );

      if (authResponse.status === 200) {
        const newToken = authResponse.data.token;

        const response = await axios.put(
          '/api/v1/users',
          { email: values.newEmail, password: values.currentPassword },
          {
            headers: {
              Authorization: `Bearer ${newToken}`,
            },
          },
        );

        setUser((prevState) => ({
          ...prevState!,
          email: response.data.email,
        }));

        // const loginWithNewEmailResponse = await axios.post(
        //   'http://localhost:8080/auth/login',
        //   { email: values.newEmail, password: values.currentPassword },
        // );
        // const parsedResult = authApiSchema.safeParse(
        //   loginWithNewEmailResponse.data,
        // );
        // if (!parsedResult.success) {
        //   setEmailError('Error updating email. Please try again.');
        //   return;
        // }
        // const validToken = parsedResult.data.token;
        // setToken(validToken);
        resetForm();
        setIsEditingEmail(false);
        setEmailError(null);
      } else {
        setEmailError('Incorrect current password. Please try again.');
      }
    } catch {
      setEmailError('Error updating email. Please try again.');
    }
  };

  const handlePasswordUpdate = async (
    values: z.infer<typeof updatePasswordSchema>,
    { resetForm }: FormikHelpers<z.infer<typeof updatePasswordSchema>>,
  ) => {
    try {
      const authResponse = await axios.post(
        'http://localhost:8080/auth/login',
        { email: user?.email, password: values.currentPassword },
      );
      if (authResponse.status === 200) {
        const newToken = authResponse.data.token;

        await axios.put(
          '/api/v1/users',
          { email: user?.email, password: values.newPassword },
          {
            headers: {
              Authorization: `Bearer ${newToken}`,
            },
          },
        );

        resetForm();
        setIsEditingPassword(false);
        setPasswordError(null);
      } else {
        setPasswordError('Incorrect current password. Please try again.');
      }
    } catch {
      setPasswordError('Error updating password. Please try again.');
    }
  };

  if (!user) return <p>Loading...</p>;

  return (
    <div className="section py-6 px-12">
      <h1 className="text-heading1-bold">User Summary</h1>

      <div className="mb-2 border-y border-gray-200 py-4">
        <h2 className="text-heading3-bold mb-4">Login Details</h2>
        {!isEditingEmail ? (
          <>
            <div className="grid grid-cols-6 mb-4">
              <strong className="col-span-1">E-mail</strong>
              <p className="col-span-4">{user?.email}</p>
              <Button
                className="bg-blue-500 hover:bg-blue-400 col-span-1"
                onClick={() => {
                  setIsEditingEmail(true);
                  setEmailError(null);
                }}
              >
                Edit
              </Button>
            </div>
          </>
        ) : (
          <div className="mb-4 rounded-lg p-4 bg-gray-100">
            {emailError && (
              <Alert variant="destructive" className="my-4 p-4">
                {emailError}
              </Alert>
            )}
            <Formik
              initialValues={{
                currentEmail: user.email,
                currentPassword: '',
                newEmail: user.email,
              }}
              validationSchema={toFormikValidationSchema(updateEmailSchema)}
              onSubmit={(values, actions) =>
                handleEmailUpdate(values, {
                  ...actions,
                })
              }
            >
              {({ isSubmitting, errors, touched }) => (
                <Form>
                  <h4 className="text-heading4-semibold mb-4">Change e-mail</h4>
                  <label className="font-semibold" htmlFor="currentEmail">
                    Current e-mail
                  </label>
                  <Field
                    className="mt-1 bg-light-1"
                    name="currentEmail"
                    type="email"
                    as={Input}
                    disabled
                  />
                  <div className="mt-4">
                    <label className="font-semibold" htmlFor="password">
                      New e-mail
                    </label>
                  </div>
                  <Field
                    className={`mt-1 bg-light-1 ${
                      errors.newEmail && touched.newEmail
                        ? 'border-red-500'
                        : ''
                    }`}
                    name="newEmail"
                    type="email"
                    as={Input}
                  />
                  <ErrorMessage
                    name="newEmail"
                    component="div"
                    render={(msg) => (
                      <div className="flex items-center mt-1 text-sm text-red-500">
                        <CircleAlert className="mr-2" />
                        <p>{msg}</p>
                      </div>
                    )}
                  />
                  <div className="mt-4">
                    <label className="font-semibold" htmlFor="password">
                      Current Password
                    </label>
                  </div>
                  <Field
                    className={`mt-1 bg-light-1 ${
                      errors.currentPassword && touched.currentPassword
                        ? 'border-red-500'
                        : ''
                    }`}
                    name="currentPassword"
                    type="password"
                    as={Input}
                  />
                  <ErrorMessage
                    name="currentPassword"
                    component="div"
                    render={(msg) => (
                      <div className="flex items-center mt-1 text-sm text-red-500">
                        <CircleAlert className="mr-2" />
                        <p>{msg}</p>
                      </div>
                    )}
                  />
                  <Button
                    className="mt-4 bg-blue-500 hover:bg-blue-400"
                    type="submit"
                    disabled={isSubmitting}
                  >
                    Update Email
                  </Button>
                  <Button
                    className="mt-4 ml-2 bg-gray-500 hover:bg-gray-400"
                    onClick={() => setIsEditingEmail(false)}
                  >
                    Cancel
                  </Button>
                </Form>
              )}
            </Formik>
          </div>
        )}
        {!isEditingPassword ? (
          <>
            <div className="grid grid-cols-6 ">
              <strong className="col-span-1">Password</strong>{' '}
              <p className="col-span-4">************</p>
              <Button
                className="bg-blue-500 hover:bg-blue-400 col-span-1"
                onClick={() => setIsEditingPassword(true)}
              >
                Edit
              </Button>
            </div>
          </>
        ) : (
          <div className="mb-4 rounded-lg p-4 bg-gray-100">
            {passwordError && (
              <Alert variant="destructive" className="my-4 p-4">
                {passwordError}
              </Alert>
            )}
            <Formik
              initialValues={{ currentPassword: '', newPassword: '' }}
              validationSchema={toFormikValidationSchema(updatePasswordSchema)}
              onSubmit={(values, actions) =>
                handlePasswordUpdate(values, {
                  ...actions,
                })
              }
            >
              {({ isSubmitting, errors, touched }) => (
                <Form>
                  <h4 className="text-heading4-semibold mb-4">
                    Change password
                  </h4>
                  <label className="font-semibold" htmlFor="currentPassword">
                    Current Password
                  </label>
                  <Field
                    className={`mt-1 bg-light-1 ${
                      errors.currentPassword && touched.currentPassword
                        ? 'border-red-500'
                        : ''
                    }`}
                    name="currentPassword"
                    type="password"
                    as={Input}
                  />
                  <ErrorMessage
                    name="currentPassword"
                    component="div"
                    render={(msg) => (
                      <div className="flex items-center mt-1 text-sm text-red-500">
                        <CircleAlert className="mr-2" />
                        <p>{msg}</p>
                      </div>
                    )}
                  />
                  <div className="mt-4">
                    <label className="font-semibold" htmlFor="newPassword">
                      New Password
                    </label>
                  </div>
                  <Field
                    className={`mt-1 bg-light-1 ${
                      errors.newPassword && touched.newPassword
                        ? 'border-red-500'
                        : ''
                    }`}
                    name="newPassword"
                    type="text"
                    as={Input}
                  />
                  <ErrorMessage
                    name="newPassword"
                    component="div"
                    render={(msg) => (
                      <div className="flex items-center mt-1 text-sm text-red-500">
                        <CircleAlert className="mr-2" />
                        <p>{msg}</p>
                      </div>
                    )}
                  />

                  <Button
                    className="mt-4 bg-blue-500 hover:bg-blue-400"
                    type="submit"
                    disabled={isSubmitting}
                  >
                    Update Password
                  </Button>
                  <Button
                    className="mt-4 ml-2 bg-gray-500 hover:bg-gray-400"
                    onClick={() => {
                      setIsEditingPassword(false);
                      setPasswordError(null);
                    }}
                  >
                    Cancel
                  </Button>
                </Form>
              )}
            </Formik>
          </div>
        )}
      </div>

      <div className="mb-2 border-b border-gray-200 py-4">
        <h2 className="text-heading3-bold mb-4">Personal Data</h2>
        <div className="grid grid-cols-2 gap-x-4 gap-y-4">
          <strong>Name</strong>
          <p>
            {user?.userInfo?.firstName} {user?.userInfo?.lastName}
          </p>

          <strong>Date of birth</strong>
          <p>{user?.userInfo?.dateOfBirth}</p>

          <strong>Phone number</strong>
          <p>{user?.userInfo?.phoneNumber}</p>
        </div>
      </div>

      <Button className="mt-4 bg-red-500 hover:bg-red-400" onClick={logout}>
        Log Out
      </Button>
    </div>
  );
}

export default Page;
