'use client';
import React, { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import axios from 'axios';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { getToken } from '@/lib/auth';
import { Calendar, Phone } from 'lucide-react';
import { UserInfo } from '@/validation/userProfileSchemas';

function Page() {
  const { id } = useParams();
  const [user, setUser] = useState<UserInfo | null>(null);
  // const [trips, setTrips] = useState<Trip[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const token = getToken();

    const fetchUserData = async () => {
      try {
        const response = await axios.get(`/api/v1/user-info/${id}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        setUser(response.data);
      } catch (err: unknown) {
        setError(`Error: ${err}`);
      }
    };
    fetchUserData();
  }, [id]);

  if (!user) {
    return <div className="text-center text-slate-400 mt-10">Loading...</div>;
  }

  if (error) {
    return <div className="text-red-500 text-center mt-10">{error}</div>;
  }

  return (
    <div className="max-w-2xl mx-auto mt-10 p-4">
      <Card className="p-6 shadow-lg rounded-2xl bg-gradient-to-r from-primary-500 to-indigo-600 text-white">
        <CardHeader className="flex items-center gap-2">
          <Avatar className="w-24 h-24 shadow-md">
            <AvatarImage src="" />
            <AvatarFallback className="text-primary-500 font-bold">
              {user.firstName.charAt(0)}
            </AvatarFallback>
          </Avatar>
          <div>
            <CardTitle className="text-3xl font-semibold">
              {user.firstName} {user.lastName}
            </CardTitle>
            <p className="text-gray-200">{user.bio}</p>
          </div>
        </CardHeader>

        <CardContent className="bg-white text-gray-800 rounded-xl p-4 shadow-md">
          <div className="flex items-center justify-around gap-4">
            <div className="flex items-center gap-2 hover:bg-slate-100 p-3 rounded-xl">
              <Phone className="w-5 h-5 text-primary-500" />
              <span className="text-gray-700">{user.phoneNumber}</span>
            </div>
            <div className="flex items-center gap-2 hover:bg-slate-100 p-3 rounded-xl">
              <Calendar className="w-5 h-5 text-primary-500" />
              <span className="text-gray-700">{user.dateOfBirth}</span>
            </div>
          </div>

          <div className="mt-6 flex justify-around">
            <div className="text-center">
              <p className="text-xl font-bold">4</p>
              <p className="text-sm text-gray-500">Trips</p>
            </div>
            <div className="text-center">
              <p className="text-xl font-bold">4</p>
              <p className="text-sm text-gray-500">Friends</p>
            </div>
          </div>

          <button className="mt-6 w-full py-3 bg-primary-500 text-white rounded-xl shadow-md hover:bg-blue-600 transition">
            Send Friend Request
          </button>
        </CardContent>
      </Card>
    </div>
  );
}

export default Page;
