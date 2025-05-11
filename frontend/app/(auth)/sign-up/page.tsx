'use client';
import React from 'react';
import { Formik, Field, Form, ErrorMessage, FormikHelpers } from 'formik';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import axios from 'axios';
import Link from 'next/link';
import { useState } from 'react';
import { Alert } from '@/components/ui/alert';
import { toFormikValidationSchema } from 'zod-formik-adapter';
import { z } from 'zod';
import { authApiSchema, registerFormSchema } from '@/validation/authSchemas';
import { CircleAlert } from 'lucide-react';
import { setToken } from '@/lib/auth';

const handleSignUp = async (
  values: z.infer<typeof registerFormSchema>,
  {
    setError,
    resetForm,
  }: FormikHelpers<z.infer<typeof registerFormSchema>> & {
    setError: (error: string) => void;
  },
) => {
  try {
    const result = await axios.post('/auth/register', values);
    const parsedResult = authApiSchema.safeParse(result.data);
    if (!parsedResult.success) {
      setError('Invalid response from server. Please try again.');
      return;
    }
    const token = parsedResult.data.token;
    setToken(token);
    window.location.href = '/trips';
    resetForm();
  } catch (err: unknown) {
    setError(
      `${
        err.response?.data?.message || 'Something went wrong'
      }. Please try again.`,
    );
    resetForm();
  }
};

function Page() {
  const [error, setError] = useState('');
  return (
    <div className="section py-6 px-12">
      <h1 className="text-heading1-bold">Sign Up</h1>
      {error && (
        <Alert variant="destructive" className="my-4 p-4">
          {error}
        </Alert>
      )}
      <Formik
        initialValues={{
          email: '',
          password: '',
          firstName: '',
          lastName: '',
          dateOfBirth: new Date(),
          phoneNumber: '',
        }}
        validationSchema={toFormikValidationSchema(registerFormSchema)}
        onSubmit={(values, actions) =>
          handleSignUp(values, { ...actions, setError })
        }
      >
        {({ isSubmitting, errors, touched, setFieldValue, values }) => (
          <Form>
            <div className="mt-4">
              <label className="font-semibold" htmlFor="email">
                Email
              </label>
              <Field
                name="email"
                type="email"
                as={Input}
                className={`mt-1 ${
                  errors.email && touched.email ? 'border-red-500' : ''
                }`}
              />
              <ErrorMessage
                name="email"
                render={(msg) => (
                  <div className="flex items-center mt-1 text-sm text-red-500">
                    <CircleAlert className="mr-2" />
                    <p>{msg}</p>
                  </div>
                )}
              />
            </div>

            <div className="mt-4">
              <label className="font-semibold" htmlFor="password">
                Password
              </label>
              <Field
                name="password"
                type="password"
                as={Input}
                className={`mt-1 ${
                  errors.password && touched.password ? 'border-red-500' : ''
                }`}
              />
              <ErrorMessage
                name="password"
                render={(msg) => (
                  <div className="flex items-center mt-1 text-sm text-red-500">
                    <CircleAlert className="mr-2" />
                    <p>{msg}</p>
                  </div>
                )}
              />
            </div>

            <div className="mt-4">
              <label className="font-semibold" htmlFor="firstName">
                First Name
              </label>
              <Field
                name="firstName"
                as={Input}
                className={`mt-1 ${
                  errors.firstName && touched.firstName ? 'border-red-500' : ''
                }`}
              />
              <ErrorMessage
                name="firstName"
                render={(msg) => (
                  <div className="flex items-center mt-1 text-sm text-red-500">
                    <CircleAlert className="mr-2" />
                    <p>{msg}</p>
                  </div>
                )}
              />
            </div>

            <div className="mt-4">
              <label className="font-semibold" htmlFor="lastName">
                Last Name
              </label>
              <Field
                name="lastName"
                as={Input}
                className={`mt-1 ${
                  errors.lastName && touched.lastName ? 'border-red-500' : ''
                }`}
              />
              <ErrorMessage
                name="lastName"
                render={(msg) => (
                  <div className="flex items-center mt-1 text-sm text-red-500">
                    <CircleAlert className="mr-2" />
                    <p>{msg}</p>
                  </div>
                )}
              />
            </div>

            <div className="mt-4">
              <label className="font-semibold" htmlFor="dateOfBirth">
                Date of Birth
              </label>
              <Field
                name="dateOfBirth"
                type="date"
                as={Input}
                value={
                  values.dateOfBirth
                    ? values.dateOfBirth.toISOString().split('T')[0]
                    : ''
                }
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                  const date = e.target.value;
                  setFieldValue('dateOfBirth', new Date(date));
                }}
                className={`mt-1 ${
                  errors.dateOfBirth && touched.dateOfBirth
                    ? 'border-red-500'
                    : ''
                }`}
              />
              <ErrorMessage
                name="dateOfBirth"
                render={(msg) => (
                  <div className="flex items-center mt-1 text-sm text-red-500">
                    <CircleAlert className="mr-2" />
                    <p>{msg}</p>
                  </div>
                )}
              />
            </div>

            <div className="mt-4">
              <label className="font-semibold" htmlFor="phoneNumber">
                Phone Number
              </label>
              <Field
                name="phoneNumber"
                type="number"
                as={Input}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                  setFieldValue('phoneNumber', e.target.value);
                }}
                className={`mt-1 ${
                  errors.phoneNumber && touched.phoneNumber
                    ? 'border-red-500'
                    : ''
                }`}
              />
              <ErrorMessage
                name="phoneNumber"
                render={(msg) => (
                  <div className="flex items-center mt-1 text-sm text-red-500">
                    <CircleAlert className="mr-2" />
                    <p>{msg}</p>
                  </div>
                )}
              />
            </div>

            <div className="flex flex-col items-center mt-6">
              <Button
                className="bg-primary-600 hover:bg-primary-500 p-6"
                type="submit"
                disabled={isSubmitting}
              >
                Sign Up
              </Button>
              <div className="mt-4 flex items-center">
                <p className="text-center">Already have an account?</p>
                <Link
                  href="/sign-in"
                  className="ml-2 text-primary-600 underline"
                >
                  Sign in here
                </Link>
              </div>
            </div>
          </Form>
        )}
      </Formik>
    </div>
  );
}

export default Page;
