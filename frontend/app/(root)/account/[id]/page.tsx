'use client';
import React, { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import axios from 'axios';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { decodeToken, getToken } from '@/lib/auth';
import {
  Calendar,
  CircleAlert,
  Pencil,
  Phone,
  Save,
  UserCheck,
  UserCog,
  UserPlus,
} from 'lucide-react';
import { UserInfo } from '@/validation/userProfileSchemas';
import { Formik, Field, Form, ErrorMessage, FormikHelpers } from 'formik';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { toFormikValidationSchema } from 'zod-formik-adapter';
import { z } from 'zod';
import { updateProfileFormSchema } from '@/validation/authSchemas';
import { toast } from 'sonner';
import { appStore } from '@/store/appStore';

function Page() {
  const { id } = useParams();
  const [userId, setUserId] = useState<string | null>(null);
  const [user, setUser] = useState<UserInfo | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const addSentFriendInvitation = appStore((s) => s.addSentFriendInvitation);
  const hasSentFriendInvitation = appStore((s) => s.hasSentFriendInvitation);
  const friends = appStore((s) => s.friends);
  const isFriend = appStore((s) => s.hasFriend);

  const fetchUserData = async (token: string, id: number) => {
    try {
      const response = await axios.get(`/api/v1/user-info/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setUser(response.data);
    } catch (err: unknown) {
      setError(`Error: ${err}`);
    }
  };

  const sendFriendRequest = async (userId: string) => {
    try {
      const token = getToken();
      await axios.post(
        `/api/v1/friend-invitations/send/${userId}`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        },
      );
      toast('Friend request sent!', {
        duration: 7000,
        action: {
          label: 'Close',
          onClick: () => {
            toast.dismiss();
          },
        },
      });
      addSentFriendInvitation(+userId);
    } catch (err) {
      setError(`Error: ${err}`);
    }
  };

  useEffect(() => {
    const token = getToken();
    if (token) {
      const decode = decodeToken(token);
      setUserId(decode?.sub ?? '');
      fetchUserData(token, Number(id));
    }
  }, [id]);

  if (!user) {
    return <div className="text-center text-slate-400 mt-10">Loading...</div>;
  }

  if (error) {
    return <div className="text-red-500 text-center mt-10">{error}</div>;
  }

  const handleSubmit = async (
    values: z.infer<typeof updateProfileFormSchema>,
    {
      setError,
      resetForm,
    }: FormikHelpers<z.infer<typeof updateProfileFormSchema>> & {
      setError: (error: string) => void;
    },
  ) => {
    try {
      const newUser = await axios.put(`/api/v1/user-info`, values, {
        headers: { Authorization: `Bearer ${getToken()}` },
      });
      setUser(newUser.data);
      resetForm();
      setIsEditing(false);

      toast('User information updated successfully!', {
        duration: 7000,
        action: {
          label: 'Close',
          onClick: () => {
            toast.dismiss();
          },
        },
      });
    } catch (err: unknown) {
      setError(`Error: ${err}`);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <Card className="p-4 shadow-lg rounded-3xl  bg-gradient-to-r from-blue-500/80 to-primary-600/85 text-white">
        {userId === id && (
          <button
            className="bg-white p-2 m-2 rounded-full shadow-md hover:bg-gray-200 transition"
            onClick={() => setIsEditing(!isEditing)}
          >
            <Pencil className="text-primary-500 w-5 h-5" />
          </button>
        )}
        <CardHeader className="flex items-center gap-2">
          <Avatar className="w-28 h-28 shadow-md">
            <AvatarImage src="" />
            <AvatarFallback className="text-primary-500 text-[2.5rem]">
              {user.firstName.charAt(0)}
            </AvatarFallback>
          </Avatar>
          <div>
            <CardTitle className="text-[1.75rem] font-semibold text-center p-2">
              {user.firstName} {user.lastName}
            </CardTitle>
            <p className="text-gray-200 text-center italic max-w-sm">
              {user.bio || 'No bio available.'}
            </p>
          </div>
        </CardHeader>

        <CardContent className="bg-white/20 rounded-3xl p-6 shadow-inner">
          {isEditing ? (
            <Formik
              initialValues={user}
              validationSchema={toFormikValidationSchema(
                updateProfileFormSchema,
              )}
              onSubmit={(values, actions) =>
                handleSubmit(values, { ...actions, setError })
              }
            >
              {({ isSubmitting, errors, touched, setFieldValue, values }) => (
                <Form>
                  <div className="mb-4">
                    <label className="font-semibold">First Name</label>
                    <Field name="firstName" as={Input} />
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
                  <div className="mb-4">
                    <label className="font-semibold">Last Name</label>
                    <Field name="lastName" as={Input} />
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
                  <div className="mb-4">
                    <label className="font-semibold">Bio</label>
                    <Field name="bio" as={Input} />
                  </div>
                  <div className="mb-4">
                    <label className="font-semibold">Phone Number</label>
                    <Field name="phoneNumber" as={Input} />
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
                  <div className="mb-4">
                    <label className="font-semibold">Date of Birth</label>
                    <Field
                      name="dateOfBirth"
                      type="date"
                      as={Input}
                      value={
                        values.dateOfBirth
                          ? new Date(values.dateOfBirth)
                              .toISOString()
                              .split('T')[0]
                          : ''
                      }
                      onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                        const date = e.target.value;
                        setFieldValue('dateOfBirth', date);
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
                  <Button
                    type="submit"
                    className="bg-white text-primary-500 hover:bg-gray-100"
                    disabled={isSubmitting}
                  >
                    <Save className="mr-2 w-4 h-4" /> Save
                  </Button>
                </Form>
              )}
            </Formik>
          ) : (
            <>
              <div className="grid grid-cols-2 gap-6 text-center">
                <div className="flex flex-col items-center bg-white/25 p-4 rounded-xl shadow">
                  <Phone className="w-6 h-6 text-primary-500" />
                  <span className="text-white mt-2">{user.phoneNumber}</span>
                </div>
                <div className="flex flex-col items-center bg-white/25 p-4 rounded-xl shadow">
                  <Calendar className="w-6 h-6 text-primary-500" />
                  <span className="text-white mt-2">
                    {new Intl.DateTimeFormat('en-US', {
                      day: '2-digit',
                      month: 'long',
                      year: 'numeric',
                    }).format(new Date(user.dateOfBirth))}
                  </span>
                </div>
                {userId !== id && (
                  <button
                    className={`col-span-2 flex items-center justify-center gap-6 px-6 py-6 text-white font-semibold rounded-xl shadow-lg transition-transform duration-200 ${
                      hasSentFriendInvitation(user.userId) ||
                      isFriend(user.userId)
                        ? 'bg-primary-700 cursor-not-allowed'
                        : 'bg-gradient-to-r from-blue-500/80 to-primary-600/85 hover:bg-gradient-to-r hover:from-blue-500 hover:to-primary-600 hover:shadow-xl'
                    }`}
                    onClick={() => sendFriendRequest(user.userId)}
                    disabled={hasSentFriendInvitation(user.userId)}
                  >
                    {isFriend(user.userId) ? (
                      <>
                        <UserCheck className="w-6 h-6" />
                        <span>Already Friends</span>
                      </>
                    ) : hasSentFriendInvitation(user.userId) ? (
                      <>
                        <UserCog className="w-6 h-6" />
                        <span className="text-white font-semibold">
                          Friend Request Sent
                        </span>
                      </>
                    ) : (
                      <>
                        <UserPlus className="w-6 h-6" />
                        <span className="text-white font-semibold">
                          Send Friend Request
                        </span>
                      </>
                    )}
                  </button>
                )}
              </div>
            </>
          )}
        </CardContent>
      </Card>
    </div>
  );
}

export default Page;
