"use client";
import { Formik, Field, Form, ErrorMessage, FormikHelpers } from "formik";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import axios from "axios";
import Link from "next/link";
import { useState } from "react";
import { Alert } from "@/components/ui/alert";
import { toFormikValidationSchema } from "zod-formik-adapter";
import { z } from "zod";
import { authApiSchema, registerFormSchema } from "@/validation/authSchemas";

const handleSignUp = async (
  values: z.infer<typeof registerFormSchema>,
  {
    setError,
    resetForm,
  }: FormikHelpers<z.infer<typeof registerFormSchema>> & {
    setError: (error: string) => void;
  }
) => {
  try {
    console.log(values);
    const result = await axios.post(
      "http://localhost:8080/auth/register",
      values
    );
    const parsedResult = authApiSchema.safeParse(result.data);
    if (!parsedResult.success) {
      setError("Invalid response from server. Please try again.");
      return;
    }
    const token = parsedResult.data.token;
    document.cookie = `token=${token}; path=/; max-age=36000; secure; samesite=strict`;
    window.location.href = "/";
    resetForm();
  } catch (err: any) {
    console.error(err);
    setError(`${err.response.data.message}. Please try again.`);
    resetForm();
  }
};

function page() {
  const [error, setError] = useState("");
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
          email: "",
          password: "",
          firstName: "",
          lastName: "",
          dateOfBirth: new Date(),
          phoneNumber: "",
        }}
        validationSchema={toFormikValidationSchema(registerFormSchema)}
        onSubmit={(values, actions) =>
          handleSignUp(values, { ...actions, setError })
        }
      >
        {({ isSubmitting }) => (
          <Form>
            <div className="mt-4">
              <label className="font-semibold" htmlFor="email">
                Email
              </label>
              <Field name="email" type="email" as={Input} />
              <ErrorMessage
                className="text-terminate-color"
                name="email"
                component="div"
              />
            </div>
            <div className="mt-4">
              <label className="font-semibold" htmlFor="password">
                Password
              </label>
              <Field name="password" type="password" as={Input} />
              <ErrorMessage
                className="text-terminate-color"
                name="password"
                component="div"
              />
            </div>
            <div className="mt-4">
              <label className="font-semibold" htmlFor="firstName">
                First Name
              </label>
              <Field name="firstName" as={Input} />
              <ErrorMessage
                className="text-terminate-color"
                name="firstName"
                component="div"
              />
            </div>
            <div className="mt-4">
              <label className="font-semibold" htmlFor="lastName">
                Last Name
              </label>
              <Field name="lastName" as={Input} />
              <ErrorMessage
                className="text-terminate-color"
                name="lastName"
                component="div"
              />
            </div>
            <div className="mt-4">
              <label className="font-semibold" htmlFor="dateOfBirth">
                Date of Birth
              </label>
              <Field name="dateOfBirth" type="date" as={Input} />
              {/* moze w przyszlosci zamiast input zrobic datepicker z shadcn ale tam przeba ogarnac wtedy formikowe rzeczy w srodku */}
              <ErrorMessage
                className="text-terminate-color"
                name="dateOfBirth"
                component="div"
              />
            </div>
            <div className="mt-4">
              <label className="font-semibold" htmlFor="phoneNumber">
                Phone Number
              </label>
              <Field name="phoneNumber" as={Input} />
              <ErrorMessage
                className="text-terminate-color"
                name="phoneNumber"
                component="div"
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
                <p className="text-center">Have an account?</p>
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

export default page;
