"use client";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Formik, Field, Form, ErrorMessage } from "formik";
import * as Yup from "yup";
import Link from "next/link";
import axios from "axios";
import { useState } from "react";
import { Alert } from "@/components/ui/alert";

const SignInSchema = Yup.object().shape({
  email: Yup.string().email("Invalid email.").required("Required"),
  password: Yup.string()
    .min(8, "Password must have 8 characters.")
    .required("Required"),
});

const handleSignIn = async (values, { setError, resetForm }) => {
  try {
    console.log(values);
    const result = await axios.post("http://localhost:8080/auth/login", values);
    const token = result.data.token;
    document.cookie = `token=${token}; path=/; max-age=36000; secure; samesite=strict`;
    window.location.href = "/";
    resetForm();
  } catch (err) {
    console.error(err);
    setError(`${err.response.data}. Please try again.`);
    resetForm();
  }
};

function page() {
  const [error, setError] = useState("");
  return (
    <div className="section py-6 px-12">
      <h1 className="text-heading1-bold">Sign In</h1>
      {error && (
        <Alert variant="destructive" className="my-4 p-4">
          {error}
        </Alert>
      )}
      <Formik
        initialValues={{ email: "", password: "" }}
        validationSchema={SignInSchema}
        onSubmit={(values, actions) =>
          handleSignIn(values, { ...actions, setError })
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
            <div className="flex flex-col items-center mt-6">
              <Button
                className="bg-primary-600 hover:bg-primary-500 p-6"
                type="submit"
                disabled={isSubmitting}
              >
                Sign In
              </Button>
              <div className="mt-4 flex items-center">
                <p className="text-center">Don't have an account?</p>
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

export default page;
