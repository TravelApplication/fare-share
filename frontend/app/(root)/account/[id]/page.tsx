'use client';
import React, { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { decodeToken, getToken } from '@/lib/auth';
import axiosInstance from '@/lib/axiosInstance';
import {
  Calendar,
  CircleAlert,
  Pencil,
  Phone,
  Save,
  UserCheck,
  UserCog,
  UserPlus,
  X,
} from 'lucide-react';
import { UserInfo } from '@/validation/userProfileSchemas';
import { Formik, Field, Form, ErrorMessage } from 'formik';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { toFormikValidationSchema } from 'zod-formik-adapter';
import { toast } from 'sonner';
import { appStore } from '@/store/appStore';
import { userInfoFormSchema } from '@/validation/userInfoFormSchema';
import { Invitation } from '@/validation/invitationsSchema';

function Page() {
  const { id } = useParams();
  const [loggedUserId, setLoggedUserId] = useState<string | null>(null);
  const [user, setUser] = useState<UserInfo | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isEditing, setIsEditing] = useState(false);
  const [sentInvitations, setSentInvitiations] = useState<Invitation[]>([]);

  const today = new Date().toISOString().split('T')[0];

  const addSentFriendInvitation = appStore((s) => s.addSentFriendInvitation);
  const isFriend = appStore((s) => s.hasFriend);

  const fetchUserData = async (id: number) => {
    try {
      const response = await axiosInstance.get(`user-info/${id}`);
      setUser(response.data);
    } catch {
      setError('An error occured while fetching user data.');
    }
  };

  const fetchInvitations = async () => {
    try {
      const response = await axiosInstance.get('friend-invitations/sent');
      setSentInvitiations(response.data);
    } catch {
      toast('Failed to load invitations', {
        description: 'Please try again later',
        duration: 5000,
        action: {
          label: 'Close',
          onClick: () => toast.dismiss(),
        },
      });
    }
  };

  const sendFriendRequest = async (userId: number) => {
    if (id && !isFriend(+id)) {
      try {
        await axiosInstance.post(`friend-invitations/send/${userId}`);
        toast('Friend request sent!', {
          duration: 7000,
          action: {
            label: 'Close',
            onClick: () => toast.dismiss(),
          },
        });
        addSentFriendInvitation(userId);
        window.location.reload();
      } catch {
        setError('An error occured while sending friend request.');
      }
    }
  };

  const hasSentFriendInvitation = (userId: number): boolean => {
    return sentInvitations.some(
      (invitation) => invitation.receiver.id === userId,
    );
  };

  useEffect(() => {
    const token = getToken();
    if (token) {
      const decode = decodeToken(token);
      setLoggedUserId(decode?.sub ?? '');
      fetchUserData(Number(id));
      fetchInvitations();
    }
  }, [id]);

  if (!user) {
    return <div className="text-center text-slate-400 mt-10">Loading...</div>;
  }

  if (error) {
    return <div className="text-red-500 text-center mt-10">{error}</div>;
  }

  return (
    <div className="max-w-2xl mx-auto">
      <Card className="p-4 shadow-lg rounded-3xl bg-gradient-to-r from-blue-500/80 to-primary-600/85 text-white">
        {loggedUserId === id && (
          <button
            className="bg-white p-2 m-2 rounded-full shadow-md hover:bg-gray-200 transition"
            onClick={() => setIsEditing(!isEditing)}
          >
            {isEditing ? (
              <X className="text-red-500 w-5 h-5" />
            ) : (
              <Pencil className="text-primary-500 w-5 h-5" />
            )}
          </button>
        )}
        <CardHeader className="flex items-center gap-2">
          <Avatar className="w-28 h-28 shadow-md">
            <AvatarImage src="" />
            <AvatarFallback className="text-primary-500 text-[2.5rem]">
              {user.firstName?.charAt(0) || 'X'}
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
              initialValues={{
                firstName: user.firstName || '',
                lastName: user.lastName || '',
                bio: user.bio || '',
                phoneNumber: user.phoneNumber || '',
                dateOfBirth: user.dateOfBirth || '',
              }}
              validationSchema={toFormikValidationSchema(userInfoFormSchema)}
              onSubmit={async (values, { setSubmitting }) => {
                try {
                  await axiosInstance.put(`user-info`, values);
                  toast.success('Profile updated successfully!');
                  setIsEditing(false);
                  fetchUserData(user.id);
                } catch {
                  toast.error('Failed to update profile.');
                } finally {
                  setSubmitting(false);
                }
              }}
            >
              {({ errors, touched, isSubmitting }) => (
                <Form>
                  <div className="mb-4">
                    <label className="font-semibold">First Name</label>
                    <Field name="firstName" className="text-black" as={Input} />
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
                    <Field name="lastName" className="text-black" as={Input} />
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
                    <Field name="bio" className="text-black" as={Input} />
                  </div>

                  <div className="mb-4">
                    <label className="font-semibold">Phone Number</label>
                    <Field
                      name="phoneNumber"
                      className="text-black"
                      as={Input}
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

                  <div className="mb-4">
                    <label className="font-semibold">Date of Birth</label>
                    <Field
                      name="dateOfBirth"
                      type="date"
                      max={today}
                      as={Input}
                      className={`mt-1 text-black ${
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
                    {user.dateOfBirth &&
                      new Intl.DateTimeFormat('en-US', {
                        day: '2-digit',
                        month: 'long',
                        year: 'numeric',
                      }).format(new Date(user.dateOfBirth))}
                  </span>
                </div>
                {loggedUserId !== id && (
                  <button
                    className={`col-span-2 flex items-center justify-center gap-6 px-6 py-6 text-white font-semibold rounded-xl shadow-lg transition-transform duration-200 ${
                      hasSentFriendInvitation(user.id) || isFriend(user.id)
                        ? 'bg-primary-700 cursor-not-allowed'
                        : 'bg-gradient-to-r from-blue-500/80 to-primary-600/85 hover:bg-gradient-to-r hover:from-blue-500 hover:to-primary-600 hover:shadow-xl'
                    }`}
                    onClick={() => sendFriendRequest(user.id)}
                    disabled={hasSentFriendInvitation(user.id)}
                  >
                    {isFriend(user.id) ? (
                      <>
                        <UserCheck className="w-6 h-6" />
                        <span>Already Friends</span>
                      </>
                    ) : hasSentFriendInvitation(user.id) ? (
                      <>
                        <UserCog className="w-6 h-6" />
                        <span>Invitation Sent</span>
                      </>
                    ) : (
                      <>
                        <UserPlus className="w-6 h-6" />
                        <span>Add Friend</span>
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
