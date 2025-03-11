'use client';
import React from 'react';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Formik, Field, Form, ErrorMessage, FormikHelpers } from 'formik';
import Link from 'next/link';
import axios from 'axios';
import { useState } from 'react';
import { Alert } from '@/components/ui/alert';
import { toFormikValidationSchema } from 'zod-formik-adapter';
import { z } from 'zod';
import { authApiSchema, loginFormSchema } from '@/validation/authSchemas';
import { CircleAlert } from 'lucide-react';
import { setToken } from '@/lib/auth';

const handleSignIn = async (
  values: z.infer<typeof loginFormSchema>,
  {
    setError,
    resetForm,
  }: FormikHelpers<z.infer<typeof loginFormSchema>> & {
    setError: (error: string) => void;
  },
) => {
  try {
    const result = await axios.post('http://localhost:8080/auth/login', values);
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
      <h1 className="text-heading1-bold">Sign In</h1>
      {error && (
        <Alert variant="destructive" className="my-4 p-4">
          {error}
        </Alert>
      )}
      <Formik
        initialValues={{ email: '', password: '' }}
        validationSchema={toFormikValidationSchema(loginFormSchema)}
        onSubmit={(values, actions) =>
          handleSignIn(values, { ...actions, setError })
        }
      >
        {({ isSubmitting, errors, touched }) => (
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
            <div className="flex flex-col items-center mt-6">
              <Button
                className="bg-primary-600 hover:bg-primary-500 p-6"
                type="submit"
                disabled={isSubmitting}
              >
                Sign In
              </Button>
              <div className="mt-4 flex items-center">
                <p className="text-center">Don&apos;t have an account?</p>
                <Link
                  href="/sign-up"
                  className="ml-2 text-primary-600 underline"
                >
                  Sign up here
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
