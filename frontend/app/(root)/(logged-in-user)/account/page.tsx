"use client";

import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import axios from "axios";
import { isAuthenticated, getToken, logout } from "@/lib/auth";
import { Button } from "@/components/ui/button";
import { Formik, Field, Form, ErrorMessage, FormikHelpers } from "formik";
import { Input } from "@/components/ui/input";
import { z } from "zod";
import { toFormikValidationSchema } from "zod-formik-adapter";
import {
  updateEmailSchema,
  updatePasswordSchema,
} from "@/validation/updateUserSchema";

interface User {
  id: number;
  email: string;
  createdAt: string;
  userInfo: UserInfo;
}

interface UserInfo {
  id: number;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  dateOfBirth: string;
}

function Page() {
  const [user, setUser] = useState<User | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isEditingEmail, setIsEditingEmail] = useState(false);
  const [isEditingPassword, setIsEditingPassword] = useState(false);
  const [currentPassword, setCurrentPassword] = useState("");
  const router = useRouter();

  useEffect(() => {
    if (!isAuthenticated()) {
      console.log("User is not authenticated, redirecting to sign-in page");
      // router.push("/sign-in");
      return;
    }

    const fetchUserData = async () => {
      try {
        const token = getToken();
        console.log("Token:", token);
        if (!token) {
          console.log("Invalid token, logging out");
          // logout();
          return;
        }

        const response = await axios.get("/api/v1/users", {
          headers: { Authorization: `Bearer ${token}` },
        });

        setUser(response.data);
        console.log("User data:", response.data);
      } catch (error) {
        console.error("Error fetching user data:", error);
        // logout();
      }
    };

    fetchUserData();
  }, [router]);

  const handleEmailUpdate = async (
    values: z.infer<typeof updateEmailSchema>,
    { resetForm }: FormikHelpers<z.infer<typeof updateEmailSchema>>
  ) => {
    try {
      console.log(values.newEmail, user?.email, values.currentPassword);
      const authResponse = await axios.post(
        "http://localhost:8080/auth/login",
        { email: user?.email, password: values.currentPassword }
      );

      if (authResponse.status === 200) {
        console.log("Auth response:", authResponse.data.token);
        const newToken = authResponse.data.token;

        const response = await axios.put(
          "/api/v1/users",
          { email: values.newEmail, password: values.currentPassword },
          {
            headers: {
              Authorization: `Bearer ${newToken}`,
            },
          }
        );

        setUser((prevState) => ({
          ...prevState!,
          email: response.data.email,
        }));

        resetForm();
        setIsEditingEmail(false);
      } else {
        setError("Incorrect current password. Please try again.");
      }
    } catch (err: any) {
      setError("Error updating email. Please try again.");
    }
  };

  const handlePasswordUpdate = async (
    values: z.infer<typeof updatePasswordSchema>,
    { resetForm }: FormikHelpers<z.infer<typeof updatePasswordSchema>>
  ) => {
    try {
      console.log(values.newPassword, values.currentPassword);
      const authResponse = await axios.post(
        "http://localhost:8080/auth/login",
        { email: user?.email, password: values.currentPassword }
      );
      if (authResponse.status === 200) {
        console.log("Auth response:", authResponse.data.token);
        const newToken = authResponse.data.token;

        const response = await axios.put(
          "/api/v1/users",
          { email: user?.email, password: values.newPassword },
          {
            headers: {
              Authorization: `Bearer ${newToken}`,
            },
          }
        );

        resetForm();
        setIsEditingPassword(false);
      } else {
        setError("Incorrect current password. Please try again.");
      }
    } catch (err: any) {
      setError("Error updating password. Please try again.");
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
                onClick={() => setIsEditingEmail(true)}
              >
                Edit
              </Button>
            </div>
          </>
        ) : (
          <div className="mb-4 rounded-lg p-4 bg-gray-100">
            <Formik
              initialValues={{ currentPassword: "", newEmail: user.email }}
              validationSchema={toFormikValidationSchema(updateEmailSchema)}
              onSubmit={(values, actions) =>
                handleEmailUpdate(values, {
                  ...actions,
                })
              }
            >
              {({ isSubmitting }) => (
                <Form>
                  <label className="font-semibold" htmlFor="password">
                    New Email
                  </label>
                  <Field
                    className="bg-light-1"
                    name="newEmail"
                    type="email"
                    as={Input}
                  />
                  <ErrorMessage
                    className="text-terminate-color"
                    name="newEmail"
                    component="div"
                  />
                  <label className="font-semibold" htmlFor="password">
                    Current Password
                  </label>
                  <Field
                    className="bg-light-1"
                    name="currentPassword"
                    type="password"
                    as={Input}
                  />
                  <ErrorMessage
                    className="text-terminate-color"
                    name="currentPassword"
                    component="div"
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
              <strong className="col-span-1">Password</strong>{" "}
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
            <Formik
              initialValues={{ currentPassword: "", newPassword: "" }}
              validationSchema={toFormikValidationSchema(updatePasswordSchema)}
              onSubmit={(values, actions) =>
                handlePasswordUpdate(values, {
                  ...actions,
                })
              }
            >
              {({ isSubmitting }) => (
                <Form>
                  <label className="font-semibold" htmlFor="currentPassword">
                    Current Password
                  </label>
                  <Field
                    className="bg-light-1"
                    name="currentPassword"
                    type="password"
                    as={Input}
                  />
                  <ErrorMessage
                    className="text-terminate-color"
                    name="currentPassword"
                    component="div"
                  />
                  <label className="font-semibold" htmlFor="newPassword">
                    New Password
                  </label>
                  <Field
                    className="bg-light-1"
                    name="newPassword"
                    type="text"
                    as={Input}
                  />
                  <ErrorMessage
                    className="text-terminate-color"
                    name="newPassword"
                    component="div"
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
                    onClick={() => setIsEditingPassword(false)}
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
        Logout
      </Button>
    </div>
  );
}

export default Page;
